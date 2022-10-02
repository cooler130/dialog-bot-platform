package com.cooler.ai.platform.service.framework.impl;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.platform.condition.BaseCondition;
import com.cooler.ai.platform.entity.*;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DialogState;
import com.cooler.ai.platform.model.*;
import com.cooler.ai.platform.service.entity.*;
import com.cooler.ai.platform.service.external.ConditionContextService;
import com.cooler.ai.platform.service.framework.DSTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.squirrelframework.foundation.fsm.*;
import org.squirrelframework.foundation.fsm.builder.ExternalTransitionBuilder;

import java.util.*;

@Service("dstJsonService")
public class DSTJsonServiceImpl implements DSTService {

    private Logger logger = LoggerFactory.getLogger(DSTJsonServiceImpl.class);

    public static final Map<String, UntypedStateMachineBuilder> stateMachineMap = new HashMap<>();

    @Qualifier("jsonTransitionService")
    @Autowired
    private TransitionService transitionService;

    @Qualifier("jsonConditionLogicService")
    @Autowired
    private ConditionLogicService conditionLogicService;

    @Qualifier("jsonConditionRuleService")
    @Autowired
    private ConditionRuleService conditionRuleService;

    @Qualifier("jsonTransformRelationService")
    @Autowired
    private TransformRelationService transformRelationService;

    @Autowired
    private ConditionContextService conditionContextService;                                                                  //由各个dm模块自己注入

    @Value("${isSingleStepExplore}")
    private boolean isSingleStepExplore;                                                                                //是否为单步寻路

    @Override
    public void fsmDSTProcess(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap) {
        String intentIdStr = dialogState.getParamValue(PC.INTENT_ID, Constant.PLATFORM_PARAM);
        Integer intentId = Integer.parseInt(intentIdStr);

        String intentName = dialogState.getParamValue(PC.INTENT_NAME, Constant.PLATFORM_PARAM);

        String intentTypeStr = dialogState.getParamValue(PC.INTENT_TYPE, Constant.PLATFORM_PARAM);                         //-1，兜底意图；0，系统意图；1，一级语义意图；2，二级语义意图
        Integer intentType = Integer.parseInt(intentTypeStr);

        String domainName = dialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);

        String taskName = dialogState.getParamValue(PC.TASK_NAME, Constant.PLATFORM_PARAM);

        String fromStateIdStr = dialogState.getParamValue(PC.FROM_STATE_ID, Constant.PLATFORM_PARAM);
        Integer currentStateId = Integer.parseInt(fromStateIdStr);
        if (currentStateId == Constant.GLOBAL_ERROR_ID || currentStateId == Constant.GLOBAL_END_ID) {
            currentStateId = Constant.GLOBAL_START_ID;
        }

        Map<String, String> classifiedResultMap = null;
        String transformIntentName = intentName;
        if (intentType == Constant.SYSTEM_INTENT || intentType == Constant.LEVEL_ONE_INTENT || intentType == Constant.LEVEL_TWO_INTENT) {            //系统意图和一二级语义意图
            UntypedStateMachineBuilder builder = stateMachineMap.get(taskName);
            if (builder == null) {
                builder = buildIntentFSM(taskName);
            }

            if (builder != null) {
                Integer fromStateId = currentStateId;
                transformIntentName = getTransformIntentName(intentName, fromStateId);                                //尝试获取转义Intent

                Map<String, String> transitionCheckMap = new HashMap<>();
                classifiedResultMap = transfer(builder, taskName, transformIntentName, fromStateId, dmRequest, dialogState, transitionCheckMap, bizDataMSMap);

                String transferredStepCountStr = classifiedResultMap.get("$" + PC.TRANSFERRED_STEP_COUNT + "$");
                int transferredStepCount = Integer.parseInt(transferredStepCountStr);

                String firstBadConditionDataJS = classifiedResultMap.get("$" + PC.FIRST_BAD_CONDITION_DATA + "$");
                BaseConditionData firstBadConditionData = JSON.parseObject(firstBadConditionDataJS, BaseConditionData.class);

                String currentStateIdStr1 = classifiedResultMap.get("$" + PC.TO_STATE_ID + "$");
                currentStateId = Integer.parseInt(currentStateIdStr1);

                //如果经过迁移，得到变迁步数为0或者迁移到了一个错误状态（当前还没有什么迁移到这状态，可能以后会有），而且FIRST_BAD_CONDITION_DATA为空（未找到原因），那么设置当前起始点为GLOBAL_START_ID，进行第二次迁移尝试
                if (fromStateId != Constant.GLOBAL_START_ID && (transferredStepCount == 0 || currentStateId == Constant.GLOBAL_ERROR_ID) && firstBadConditionData == null) {
                    transformIntentName = getTransformIntentName(intentName, Constant.GLOBAL_START_ID);               //尝试获取转义Intent

                    classifiedResultMap = transfer(builder, taskName, transformIntentName, Constant.GLOBAL_START_ID, dmRequest, dialogState, transitionCheckMap, bizDataMSMap);           //todo:第二次transfer不知道是否应该使用转义意图，先用着看效果
                    String currentStateIdStr2 = classifiedResultMap.get("$" + PC.TO_STATE_ID + "$");
                    String transferredStepCountStr2 = classifiedResultMap.get("$" + PC.TRANSFERRED_STEP_COUNT + "$");
                    int transferredStepCount2 = Integer.parseInt(transferredStepCountStr2);
                    currentStateId = Integer.parseInt(currentStateIdStr2);
                    String firstBadConditionDataJS2 = classifiedResultMap.get("$" + PC.FIRST_BAD_CONDITION_DATA + "$");

                    if(transferredStepCount2 > 0){                                                                      //如果第二次尝试变迁成功
                        if(currentStateId == Constant.GLOBAL_START_ID){                                                     //如果从GLOBAL_START_ID尝试变迁成功，但还是变迁到GLOBAL_START_ID状态，那么强制回到进入状态机时的状态，如下为解释。
                            //解释：下面if体内部的意思是：如果第二次从GLOBAL_START_ID作为起始状态的变迁得到的终止状态还是GLOBAL_START_ID，那么强制将当前状态恢复到最初的fromStateId，因为这次可能是碰到了一个上下文无关的非流程性意图。
                            //（在状态机内部任意状态下都可能遇到FAQ相关意图，此类意图就是一问一答，跟上下文无关，也不会导致状态变迁。假设在A状态遇到FAQ相关意图，还是会处理一次（但不会有变迁的），为简化流程，规定FAQ意图统一在GLOBAL_START_ID作为起始状态处理，处理后，再强制回到状态机的A状态。）
                            //（但如果状态机A状态遇到了非FAQ相关意图，又无法处理，则在GLOBAL_ERROR_ID进行尝试处理后能够产生状态变迁（就是遇到了一个跳出上下文的意图，在GLOBAL_ERROR_ID作为起始状态尝试处理成功），那么就不强制回到状态机的A状态了。）
                            currentStateId = fromStateId;
                        }else{ }                                                                                            //如果从GLOBAL_START_ID尝试变迁成功，而且变迁到其它状态了，那么终止状态就是其它状态
                    }else{                                                                                              //如果第二次尝试变迁也失败了，没有原因，就进入全局错误状态。
                        if(firstBadConditionDataJS2 == null) currentStateId = EntityConstant.ERROR_STATE_ID;
                    }
                }
                dialogState.addToModelStateMap(Constant.DST_TRANSITION_CHECK_MAP, JSON.toJSONString(transitionCheckMap));

            } else {
                currentStateId = EntityConstant.ERROR_STATE_ID;
                logger.error("2.2.d.没有办法构建状态机，请检测数据配置是否正确！（domainName: " + domainName + "intentId: " + intentId + "，或者taskId：" + taskName + " ）");
            }
        } else if (intentType == Constant.DEFAULT_INTENT) {                                                     //TODO： 默认意图
            currentStateId = EntityConstant.END_STATE_ID;
            logger.debug("2.2.d.行为意图，无需检测，状态直接到结束，代表发生行为后直接执行动作，直接结束！");
        } else {
            currentStateId = EntityConstant.ERROR_STATE_ID;
            logger.error("2.2.f.系统错误，系统没有设置这类意图，意图类型为：" + intentType);
        }

        for (String classifiedKey : classifiedResultMap.keySet()) {
            dialogState.addToParamValueMapDirectly(classifiedKey, classifiedResultMap.get(classifiedKey));              //已经分好类的key直接加入
        }
        dialogState.addToParamValueMap(PC.TO_STATE_ID, currentStateId + "", Constant.PLATFORM_PARAM);         //resultMap中的currentStateId可能只是阶段性的数据，不准确，所以要重设
        dialogState.addToParamValueMap(PC.TRANSFORM_INTENT_NAME, transformIntentName, Constant.PLATFORM_PARAM);

    }

    /**
     * 获取转义意图
     * @param contextIntentName 当前语境意图
     * @param contextStateId    当前语境状态
     * @return
     */
    private String getTransformIntentName(String contextIntentName, Integer contextStateId){
        TransformRelation transformRelation = transformRelationService.selectByContextStateIdIntent(contextStateId, contextIntentName);
        if(transformRelation != null){
            String transformIntentName = transformRelation.getTransformIntentName();
            return transformIntentName;
        }
        return contextIntentName;
    }

    /**
     * 通过taskId，获取此task的状态机
     * @param taskName
     * @return
     */
    private UntypedStateMachineBuilder buildIntentFSM(String taskName) {

        List<Transition> transitions = new ArrayList<>();                                                               //Transition处理：（添加全局起始到终止状态迁移）准备一个Map<transitionId, Transition>，将transitions集合放到一个Map中
//        transitions.add(Transition.getStartEndInstance());
        List<Transition> transitionsInTask = transitionService.selectByTaskName(taskName);
        if (transitionsInTask != null && transitionsInTask.size() > 0) {
            transitions.addAll(transitionsInTask);
        } else {
            logger.warn("提示，taskName：" + taskName + "没有查询到任何Transition记录！");
        }

        UntypedStateMachineBuilder builder = StateMachineBuilderFactory.create(StateMachineSample.class, Integer.class, String.class, ConditionContext.class);               //构建domainId、intentId下的状态机
        ExternalTransitionBuilder etBuilder = builder.externalTransition();
        for (Transition transition : transitions) {
            String transitionName = transition.getTransitionName();
            Integer fromStateId = transition.getStartStateId();
            Integer toStateId = transition.getEndStateId();
            etBuilder.from(fromStateId).to(toStateId).on(transitionName).when(new BaseCondition(transitionName)).callMethod("fromOneToOne");
        }
        stateMachineMap.put(taskName + "", builder);
        return builder;
    }

    /**
     * 此方法为迁移过程
     * @param builder
     * @param currentStateId
     * @param taskName
     * @param dialogState
     * @return
     */
    private Map<String, String> transfer(UntypedStateMachineBuilder builder, String taskName, String currentIntentName, Integer currentStateId, DMRequest dmRequest, DialogState dialogState, Map<String, String> transitionCheckMap, Map<String, BizDataModelState<String>> bizDataMSMap){
        int transferredStepCount = 0;
        boolean lastStateTransferSuccessed = true;                                                                      //上一个状态变迁已经成功变迁
        boolean thisHasTransitionsCanTry = true;                                                                        //当前状态有可尝试的变迁
        boolean currentLastStepInARing = false;                                                                         //当前是环状迁移的最后一步了

        Set<String> stateRecordSet = new HashSet<>();                                                                   //state记录（用来跳出环状迁移路径）
        Integer fromStateId = currentStateId.intValue();
        List<Transition> selectedTransitions = new ArrayList<>();
        Map<String, String> conditionDataValuesMap = new HashMap<>();                                                   //成功迁移的变迁，所用到的所有ConditionData对象
        ConditionContext lastSuccessConditionContext = null;
        ConditionData firstBadConditionData = null;                                                                     //第一个导致变迁失败的变迁条件

        A:while(lastStateTransferSuccessed && thisHasTransitionsCanTry && !currentLastStepInARing){                     //如果经过测试，前一个状态下（有任一变迁成功） 并且 当前状态下 （有变迁能去尝试） 而且 前一个成功变迁 （前后状态不相同）  则不跳出循环
            UntypedStateMachine stateMachine = builder.newStateMachine(currentStateId);

            List<Transition> transitions = transitionService.selectByTaskStartStateId(taskName, currentStateId, currentIntentName);              //找到当前状态编号下所有变迁
            if (transitions != null && transitions.size() > 0) {
                stateRecordSet.add(currentStateId + "");                                                                //当开始尝试此state的变迁前，先记录此state
                int size = transitions.size();
                B:for (int num = 0; num < size; num ++) {                                                               //遍历这些变迁，看看哪一个变迁的条件被满足
                    Transition transition = transitions.get(num);
                    String transitionName = transition.getTransitionName();
                    Integer transitionId = transition.getId();
                    Integer startStateId = transition.getStartStateId();
                    Integer endStateId = transition.getEndStateId();

                    ConditionContext conditionContext = new ConditionContext(transitionName);

                    Set<Integer> transitionIds = new HashSet<>();
                    transitionIds.add(transitionId);
                    List<ConditionLogic> conditionLogics = conditionLogicService.selectByTransitionIds(transitionIds);
                    Set<Integer> conditionRuleIds = null;
                    if (conditionLogics != null && conditionLogics.size() > 0) {
                        conditionRuleIds = new HashSet<>();
                        for (ConditionLogic conditionLogic : conditionLogics) {
                            conditionRuleIds.add(conditionLogic.getConditionRuleId());
                        }
                    }
                    if (conditionRuleIds != null && conditionRuleIds.size() > 0) {
                        List<ConditionRule> conditionRules = conditionRuleService.selectByConditionRuleIds(conditionRuleIds);
                        Map<Integer, ConditionRule> idConditionRuleMap = new HashMap<>();
                        for (ConditionRule conditionRule : conditionRules) {
                            idConditionRuleMap.put(conditionRule.getId(), conditionRule);
                        }

                        for (int i = 0; i < conditionLogics.size(); i++) {
                            ConditionLogic conditionLogic = conditionLogics.get(i);
                            Integer conditionRuleId = conditionLogic.getConditionRuleId();
                            ConditionRule conditionRule = idConditionRuleMap.get(conditionRuleId);
                            String paramsName = conditionRule.getParamName();
                            boolean isHadBePreprocessed = conditionContext.hadBePreprocessed(paramsName);               //其实只有业务参数会预处理
                            ConditionData conditionData = conditionContextService.getConditionData(transition, conditionRule, conditionLogic, dmRequest, dialogState, bizDataMSMap, isHadBePreprocessed);
                            conditionContext.addConditionData(conditionData);
                            conditionContext.recordPreprocessedParamName(paramsName);                                   //预处理后要进行记录

                            Integer paramType = conditionData.getParamType();
                            String conditionDataParam = conditionData.getParamsName();
                            String conditionDataValue = (String) conditionData.getValue();
                            if(paramType == Constant.SLOT_PARAM){
                                conditionDataValuesMap.put(conditionDataParam, conditionDataValue);       //将conditionData的数据保存起来，传出去，留以后用，由于通过conditionContextService得到
                            }else if(paramType == Constant.CUSTOM_PARAM){
                                conditionDataValuesMap.put("#" + conditionDataParam + "#", conditionDataValue);
                            }else if(paramType == Constant.PLATFORM_PARAM){
                                conditionDataValuesMap.put("$" + conditionDataParam + "$", conditionDataValue);
                            }else if(paramType == Constant.BIZ_PARAM){
                                conditionDataValuesMap.put("%" + conditionDataParam + "%", conditionDataValue);
                            }
                        }

                    }
                    stateMachine.fire(transitionName, conditionContext);
                    Boolean transitionResult = conditionContext.getLastResult();                                        //在此变迁过程中执行成功与否，就看里面存储的执行结果了
                    String allResultProcess = conditionContext.getAllResultProcess();
                    transitionCheckMap.put(startStateId + "-" + endStateId + "_result", transitionResult + "");
                    transitionCheckMap.put(startStateId + "-" + endStateId + "_process", allResultProcess);

                    logger.info(startStateId + "->" + endStateId + "变迁 " + (transitionResult ? "成功" : "失败") + allResultProcess);

                    if(transitionResult){
                        Integer changedStateId = (Integer) stateMachine.getCurrentState();
                        logger.debug(" 发生状态变迁 ： 变迁 <" + transitionId + "> ，旧状态（" + currentStateId + "） ---> 新状态（" + changedStateId + "）");
                        if(stateRecordSet.contains(changedStateId.intValue() + "")){                                    //TODO：检测迁移状态记录里面是否包含最后一次状态，如果有说明有环状迁移路径，那么就将倒数第二次作为当前状态（破除环状迁移），此做法有待验证！！！
                            currentLastStepInARing = true;
                            logger.debug(" 虽然 旧状态（" + currentStateId + "） == 新状态（" + changedStateId + "），但此变迁依旧成功！！！");
                        }else{                                                                                          //不包含，则正常迁移
                            currentStateId = changedStateId;
                        }
                        selectedTransitions.add(transition);
                        lastStateTransferSuccessed = true;
                        lastSuccessConditionContext = conditionContext;
                        transferredStepCount ++;
                        if(isSingleStepExplore){                                                                        //如果是单步寻路，则一旦变迁成功一次，就立刻跳出；如果不是单步寻路，则继续寻路。
                            break A;
                        }
                        break B;                                                                                        //一旦变迁成功，就跳出循环，不需要检测下一个变迁条件了。
                    } else{
                        logger.debug(" 不发生状态变迁 ： 变迁 <" + transitionId + "> ，状态（" + currentStateId + "）不变，上下文 [ " + JSON.toJSONString(conditionContext) + " ] ");
                        firstBadConditionData = conditionContext.findFirstBadConditionData();
                        lastStateTransferSuccessed = false;
                    }
                }
            } else {
                logger.warn("任务： " + taskName + " ，在状态： " + currentStateId + " 下，没有找到变迁条件。");
                thisHasTransitionsCanTry = false;
            }
        }

        Map<String, String> classifiedResultMap = new HashMap<>();
        classifiedResultMap.putAll(conditionDataValuesMap);

        classifiedResultMap.put("$" + PC.FROM_STATE_ID2 + "$", fromStateId + "");
        classifiedResultMap.put("$" + PC.TO_STATE_ID + "$", currentStateId + "");
        classifiedResultMap.put("$" + PC.TRANSFERRED_STEP_COUNT + "$", transferredStepCount + "");
        if(firstBadConditionData != null) classifiedResultMap.put("$" + PC.FIRST_BAD_CONDITION_DATA + "$", JSON.toJSONString(firstBadConditionData));

        if(selectedTransitions.size() > 0) classifiedResultMap.put("$" + PC.SELECTED_TRANSITIONS + "$", JSON.toJSONString(selectedTransitions));
        if(lastSuccessConditionContext != null) classifiedResultMap.put("$" + PC.LAST_SUCCESS_CONDITION_CONTEXT + "$", JSON.toJSONString(lastSuccessConditionContext));

        return classifiedResultMap;
    }

}
