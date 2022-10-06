package com.cooler.ai.platform.service.framework.impl;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.platform.action.InteractiveTaskAction;
import com.cooler.ai.platform.action.ProcessedTaskAction;
import com.cooler.ai.platform.entity.*;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DMResponse;
import com.cooler.ai.platform.facade.model.DialogState;
import com.cooler.ai.platform.model.*;
import com.cooler.ai.platform.service.entity.ActionService;
import com.cooler.ai.platform.service.entity.ConditionKVService;
import com.cooler.ai.platform.service.entity.PolicyService;
import com.cooler.ai.platform.service.framework.PolicyProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("policyProcessService")
public class PolicyProcessServiceImpl implements PolicyProcessService {

    private Logger logger = LoggerFactory.getLogger(PolicyProcessServiceImpl.class);

    @Qualifier("policyService")
    @Autowired
    private PolicyService policyService;

    @Qualifier("conditionKVService")
    @Autowired
    private ConditionKVService conditionKVService;

    @Qualifier("actionService")
    @Autowired
    private ActionService actionService;

    @Autowired
    private TaskActionsHolder taskActionsHolder;

    @Override
    public Action queryPolicy(DialogState dialogState) {
        logger.debug("3.1.---------------------------查询策略（level1）");

        //1.利用dst结果进行决策策略（看看dst是否变迁成功，如果变迁不成功，则进行必须槽位检测，如果有缺失必须槽位，则进行槽位追问动作，其ID为INQUIRY_ACTION_ID）
        String toStateIdStr = dialogState.getParamValue(PC.TO_STATE_ID, Constant.PLATFORM_PARAM);
        Integer toStateId = Integer.parseInt(toStateIdStr);
        if(toStateId == EntityConstant.ERROR_STATE_ID){
            return Action.getDefaultAction();
        }

        String selectedTransitions = dialogState.getParamValue(PC.SELECTED_TRANSITIONS, Constant.PLATFORM_PARAM);
        String firstBadConditionDataJS = dialogState.getParamValue(PC.FIRST_BAD_CONDITION_DATA, Constant.PLATFORM_PARAM);
        BaseConditionData firstBadConditionData = JSON.parseObject(firstBadConditionDataJS, BaseConditionData.class);
        if(selectedTransitions == null){                                                                                //如果没有"selectedTransitions"，说明dst阶段状态维持原始状态，没有变迁成功
            if(firstBadConditionData != null){                                                                          //  1.没有变迁成功，但还是找到了失败原因
                ConditionLogic conditionLogic = firstBadConditionData.getConditionLogic();
                byte noPassNotice = 0;
                if(conditionLogic != null){
                    noPassNotice = conditionLogic.getNopassNotice();
                }
                if(noPassNotice == (byte)1) return Action.getInquiryAction();                                           //  只有firstBadConditionData有值，又需要提示/询问（noPassNotice == 1），才去提示/询问，前面的逻辑，有需要提示/询问，则肯定优先返回的，见ConditionContext.findFirstBadConditionData()。
            }else{                                                                                                      //  2.没有变迁成功，还没找到失败原因，则兜底
                return Action.getDefaultAction();
            }
        }

        //2.确认了变迁已经成功，使用组合码（stateId+intentId）查找动作策略，并进行状态迁移DST，即改变intentState
        String transformIntentName = dialogState.getParamValue(PC.TRANSFORM_INTENT_NAME, Constant.PLATFORM_PARAM);
        String intentName = dialogState.getParamValue(PC.INTENT_NAME, Constant.PLATFORM_PARAM);
        boolean isSame = transformIntentName.equals(intentName);
        String[] selectIds = null;
        List<Policy> policies = null;
        Policy targetPolicy = null;

        policies = policyService.selectByIntentStateId(transformIntentName, toStateId);
        targetPolicy = checkPolicies(policies, dialogState);
        selectIds = new String[]{toStateId + "", transformIntentName};

        if(targetPolicy == null && !isSame){
            policies = policyService.selectByIntentStateId(intentName, toStateId);
            targetPolicy = checkPolicies(policies, dialogState);
            selectIds = new String[]{toStateId + "", intentName};
        }
        if(targetPolicy == null){                                                                   //如果currentStateId + currentIntentId得不到策略集，则尝试START_STATE_ID + currentIntentId，可能意图为faq意图
            policies = policyService.selectByIntentStateId(transformIntentName, EntityConstant.START_STATE_ID);
            targetPolicy = checkPolicies(policies, dialogState);
            selectIds = new String[]{EntityConstant.START_STATE_ID + "", transformIntentName};
        }
        if(targetPolicy == null && !isSame){                                                                   //如果currentStateId + currentIntentId得不到策略集，则尝试START_STATE_ID + currentIntentId，可能意图为faq意图
            policies = policyService.selectByIntentStateId(intentName, EntityConstant.START_STATE_ID);
            targetPolicy = checkPolicies(policies, dialogState);
            selectIds = new String[]{EntityConstant.START_STATE_ID + "", intentName};
        }
        if(targetPolicy == null){
            policies = policyService.selectByIntentStateId(EntityConstant.ANY_INTENT, toStateId);
            targetPolicy = checkPolicies(policies, dialogState);
            selectIds = new String[]{toStateId + "", EntityConstant.ANY_INTENT};
        }
        if(targetPolicy == null && toStateId != EntityConstant.START_STATE_ID){
            policies = policyService.selectByIntentStateId(EntityConstant.ANY_INTENT, EntityConstant.START_STATE_ID);
            targetPolicy = checkPolicies(policies, dialogState);
            selectIds = new String[]{EntityConstant.START_STATE_ID + "", EntityConstant.ANY_INTENT};
        }
        logger.debug("3.1.c.经过选择，最终使用stateId: {}, 使用intentName: {} ，得到policies ： {}", selectIds[0], selectIds[1], JSON.toJSON(policies));

        if(targetPolicy == null){
            logger.error("3.1.d.系统错误！！！！当前intentId：  [ " + intentName + " -> " + transformIntentName + " ]，当前stateId：  " + toStateId +   "（以及START_STATE_ID（1）状态），都没有配置动作策略！" );
            targetPolicy = Policy.DEFAULT_POLICY;                                                        //如果policies没东西，则兜底策略加进去，作为保底
        }

        dialogState.addToParamValueMap(PC.POLICY_ID, targetPolicy.getId() + "", Constant.PLATFORM_PARAM);
        Integer startActionId = targetPolicy.getStartActionId();
        Action startAction = actionService.getActionById(startActionId);

        return startAction;
    }

    private Policy checkPolicies(List<Policy> policies, DialogState dialogState){
        if(policies == null || policies.size() == 0) return null;
        Map<Integer, Policy> policyMap = new HashMap<>();
        for (Policy policy : policies) {
            Integer policyId = policy.getId();
            policyMap.put(policyId, policy);
        }
        Map<Integer, List<ConditionKV>> conditionKVMap = new HashMap<>();
        List<ConditionKV> conditionKVs = conditionKVService.getConditionKVsByPolicyIds(policyMap.keySet());
        if(conditionKVs != null){                                                                                       //如果校验条件集合不为空，则需要先分组：Map<policyId, List<ConditionKV>>
            for (ConditionKV conditionKV : conditionKVs) {
                Integer policyId = conditionKV.getPolicyId();
                List<ConditionKV> conditionKVGroup = conditionKVMap.get(policyId);
                if(conditionKVGroup == null){
                    conditionKVGroup = new ArrayList<>();
                }
                conditionKVGroup.add(conditionKV);
                conditionKVMap.put(policyId, conditionKVGroup);
            }

            for (Integer policyId : policyMap.keySet()) {
                List<ConditionKV> conditionKVGroup = conditionKVMap.get(policyId);
                if(conditionKVGroup == null){
                    return policyMap.get(policyId);
                }else{
                    boolean currentPolicyResult = getPolicyConditionKV(conditionKVGroup, dialogState);                  //此时经过slotOperate和dst，槽位和组合数据都是完备的。
                    if(currentPolicyResult){
                        return policyMap.get(policyId);
                    }
                }
            }
        }
        return null;
    }

    private boolean getPolicyConditionKV(List<ConditionKV> conditionKVs, DialogState dialogState){
        //1.对conditionKV集合进行分组（组内为或、组间为并）
        Map<Integer, List<ConditionKV>> conditionKVGroupMap = new HashMap<>();
        for (ConditionKV conditionKV : conditionKVs) {
            Integer groupNum = conditionKV.getGroupNum();
            List<ConditionKV> conditionKVGroupItem = conditionKVGroupMap.get(groupNum);
            if(conditionKVGroupItem == null){
                conditionKVGroupItem = new ArrayList<>();
            }
            conditionKVGroupItem.add(conditionKV);
            conditionKVGroupMap.put(groupNum, conditionKVGroupItem);
        }

        //2.根据不同的conditionKV，获取其key、value、relationship、logicType，组成一个个校验组（其中：？的含义有logicType决定，总体默认为false） lastResult = false || ( true ? x1 ? x2 ? x3) || (true ? y1 ? y2 ? y3) || (true ? z1 ? z2 ? z3)
        StringBuilder sb = new StringBuilder("false || ");
        boolean startResult = false;
        Set<Integer> groupNums = conditionKVGroupMap.keySet();
        Collections.sort(new ArrayList(groupNums), Comparator.reverseOrder());
        for (Integer groupNum : groupNums) {
            List<ConditionKV> conditionKVGroupItem = conditionKVGroupMap.get(groupNum);
            sb.append(" ( ");
            boolean groupResult = true;
            for (ConditionKV conditionKV : conditionKVGroupItem) {
                String key = conditionKV.getConditionKey();
                String slotValue = dialogState.getParamValue(key, Constant.UNKNOWN_PARAM);                 //这个key用来查询本轮得到的槽位数据（不能确定是系统槽位、业务槽位还是合成槽位）
                String conditionValue = conditionKV.getConditionValue();

                Integer relationship = conditionKV.getRelationship();
                boolean itemResult = false;
                if(slotValue != null){
                    switch (relationship){
                        case Constant.R_EQUAL : {
                            if(slotValue.equals(conditionValue))    itemResult = true;
                            break;
                        }
                        case Constant.R_NO_EQUAL : {
                            if(!slotValue.equals(conditionValue))   itemResult = true;
                            break;
                        }
                        case Constant.R_MORE : {
                            try{
                                int businessValueIntValue = Integer.parseInt(slotValue);
                                int conditionValueIntValue = Integer.parseInt(conditionValue);
                                if(businessValueIntValue > conditionValueIntValue)  itemResult = true;
                            }catch (Exception e){
                                logger.error("ConditionKV设置错误（比较大小，却不是数值）！其Id号为：{}", conditionKV.getId());
                            }
                            break;
                        }
                        case Constant.R_MORE_EQUAL : {
                            try{
                                int businessValueIntValue = Integer.parseInt(slotValue);
                                int conditionValueIntValue = Integer.parseInt(conditionValue);
                                if(businessValueIntValue >= conditionValueIntValue)  itemResult = true;
                            }catch (Exception e){
                                logger.error("ConditionKV设置错误（比较大小，却不是数值）！其Id号为：{}", conditionKV.getId());
                            }
                            break;
                        }
                        case Constant.R_LESS : {
                            try{
                                int businessValueIntValue = Integer.parseInt(slotValue);
                                int conditionValueIntValue = Integer.parseInt(conditionValue);
                                if(businessValueIntValue < conditionValueIntValue)  itemResult = true;
                            }catch (Exception e){
                                logger.error("ConditionKV设置错误（比较大小，却不是数值）！其Id号为：{}", conditionKV.getId());
                            }
                            break;
                        }
                        case Constant.R_LESS_EQUAL : {
                            try{
                                int businessValueIntValue = Integer.parseInt(slotValue);
                                int conditionValueIntValue = Integer.parseInt(conditionValue);
                                if(businessValueIntValue <= conditionValueIntValue)  itemResult = true;
                            }catch (Exception e){
                                logger.error("ConditionKV设置错误（比较大小，却不是数值）！其Id号为：{}", conditionKV.getId());
                            }
                            break;
                        }
                        case Constant.R_CONTAIN : {
                            if(slotValue.contains(conditionValue))      itemResult = true;
                            break;
                        }
                        case Constant.R_NOT_CONTAIN : {
                            if(!slotValue.contains(conditionValue))      itemResult = true;
                            break;
                        }
                        case Constant.R_BE_CONTAINED : {
                            String[] conditionValueItems = conditionValue.split(",");
                            for (String conditionValueItem : conditionValueItems) {
                                if(conditionValueItem.equals(slotValue)){
                                    itemResult = true;
                                    break;
                                }
                            }
                            break;
                        }
                        case Constant.R_NOT_BE_CONTAINED : {
                            itemResult = true;
                            String[] conditionValueItems = conditionValue.split(",");
                            for (String conditionValueItem : conditionValueItems) {
                                if(conditionValueItem.equals(slotValue)){
                                    itemResult = false;
                                    break;
                                }
                            }
                            break;
                        }
                        case Constant.R_MATCH_REGEXP : {
                            if(slotValue.matches(conditionValue))   itemResult = true;
                            break;
                        }
                        case Constant.R_NOT_MATCH_REGEXP : {
                            if(!slotValue.matches(conditionValue))   itemResult = true;
                            break;
                        }
                    }
                } else {
                    if(relationship == Constant.R_NONE){
                        itemResult = true;
                    }
                }

                //获取上面的itemResult，再进行下面的逻辑运算
                int logicType = (int)conditionKV.getLogicType();                                                            //1：并；  0：或； -1：非
                switch (logicType){
                    case Constant.AND : {
                        sb.append(groupResult).append(" && ").append(itemResult);
                        groupResult = groupResult && itemResult;
                        break;
                    }
                    case Constant.OR : {
                        sb.append(groupResult).append(" || ").append(itemResult);
                        groupResult = groupResult || itemResult;
                        break;
                    }
                    case Constant.NOT : {
                        sb.append(groupResult).append(" && !").append(itemResult);
                        groupResult = groupResult && !itemResult;
                        break;
                    }
                }
            }
            sb.append(" ) ");

            //获取了上面的groupResult，在进行合并
            startResult = startResult || groupResult;
        }
        System.out.println("逻辑算式：" + sb.toString() + " = " + startResult);

        return startResult;
    }

    @Override
    public DMResponse runActions(DMRequest dmRequest, DialogState dialogState, Action startAction, Map<String, BizDataModelState<String>> bizDataMap) {
        return runAction(dmRequest, dialogState, startAction, bizDataMap);
    }

    private DMResponse runAction(DMRequest dmRequest, DialogState dialogState, Action currentAction, Map<String, BizDataModelState<String>> bizDataMap) {
        Integer actionId = currentAction.getId();
        String actionName = currentAction.getActionName();
        Integer actionType = currentAction.getActionType().intValue();
        String processCode = currentAction.getProcessCode();
//        String defaultReply = currentAction.getDefaultReply();
        String nextProcessCode = null;

        logger.debug("start to run action, actionId: {}, actionType：{}, actionName: {} ,processCode: {}", actionId, actionType == Constant.PROCESSED_ACTION ? "PROCESS_ACTION" : "INTERACTIVE_ACTION", actionName, processCode);

        Map<String, BizDataModelState<String>> bizDataMapTmp = null;
        if(actionType == Constant.PROCESSED_ACTION){                                                            //处理动作，（在db中设定了nextId的动作，主要在里面写逻辑）
            ProcessedTaskAction action = (ProcessedTaskAction) taskActionsHolder.getTaskActionMap().get(processCode);
            action.setData(dmRequest, dialogState, bizDataMap);
            bizDataMapTmp = action.process();
            if(bizDataMapTmp != null && bizDataMapTmp.size() > 0) {
                bizDataMap.putAll(bizDataMapTmp);     //注意：bizDataMSMap是个积累量，它不会放入到本轮的dialogState里面，因为它不全部是本轮起始的状态数据，本轮计算出来的业务参数并没有参与本轮计算。但它的canKeep部分会保存到cache中，作为后面轮次的上下文被使用，后面轮次会在后续ContextService阶段继续取出来，在后续填槽阶段填入后面DialogState中作为业务参数。
            }
            nextProcessCode = action.routeNextProcessCode();
        }
        else if(actionType == Constant.INTERACTIVE_ACTION) {                                                    //交互动作，（末尾返回dmResponse的动作，主要在里面进行数据封装）
            //Constant.REPLY是必须要有的数据，所以每一次执行唯一的交互动作之前一定要准备一个默认的，没有默认的也要赋予一个空数据（"none"值），以防止上一轮的Constant.REPLY被用到本轮来，至于这个默认的用不用得上，要看后面交互动作中会不会重写createReplyMessage()方法，准备新的reply数据。
//            if(defaultReply != null && !defaultReply.equals("none")) bizDataMap.put(Constant.REPLY, new BizDataModelState<>(dialogState.getSessionId(), dialogState.getTotalTurnNum(), dialogState.getDomainName(), dialogState.getTaskName(), dialogState.getDomainTaskTurnNum(), dialogState.getBotName(), Constant.REPLY, defaultReply, Constant.FOREVER));
//            else bizDataMap.put(Constant.REPLY, new BizDataModelState<>(dialogState.getSessionId(), dialogState.getTotalTurnNum(), dialogState.getDomainName(), dialogState.getTaskName(), dialogState.getDomainTaskTurnNum(), dialogState.getBotName(), Constant.REPLY, "none", Constant.FOREVER));

            InteractiveTaskAction action = (InteractiveTaskAction) taskActionsHolder.getTaskActionMap().get(processCode);
            action.setProcessActionName(action.getClass().getSimpleName());
            action.setData(dmRequest, dialogState, bizDataMap);
            DMResponse dmResponse = action.interActive();

            //修复状态（无论从什么状态经过变迁到达了global_error（id=3）），都要进行状态恢复，这样虽然本轮出现错误情况，但此逻辑恢复到出现错误之前的状态，分为两部分:
            // 如果from_state_id是global_start，则将from_state_id恢复到last_from_state_id（没有last_from_state_id则保持为global_start）
            // 然后将from_state_id赋予to_state_id
            String toStateId = dialogState.getParamValue(PC.TO_STATE_ID, Constant.PLATFORM_PARAM);
            if(toStateId.equals(EntityConstant.ERROR_STATE_ID + "")){
                String fromStateId = dialogState.getParamValue(PC.FROM_STATE_ID, Constant.PLATFORM_PARAM);
                toStateId = fromStateId;
                dialogState.addToParamValueMap(PC.TO_STATE_ID, toStateId, Constant.PLATFORM_PARAM);
                dmResponse.setMsg("提示：经过状态修复，修复后fromStateId为：" + fromStateId + "，toStateId为：" + toStateId);
            }

            return dmResponse;
        }

        if(nextProcessCode != null && !nextProcessCode.equals(EntityConstant.NONE_ACTION_PROCESSCODE)){
            Action nextAction = actionService.getActionByProcessCode(nextProcessCode);
            if(nextAction == null){
                logger.error("系统错误！ ProcessCode为：" + processCode + "，没有配置相关的动作，请系统管理员检查。现在执行兜底动作！");
                nextAction = Action.getDefaultAction();
            }
            return runAction(dmRequest, dialogState, nextAction, bizDataMap);
        }

        DMResponse dmResponse = new DMResponse(0, "fail", dialogState.getBotName(), dmRequest.getSessionId(), dialogState.getTotalTurnNum(), null, null, null, 0l, 0l);
        return dmResponse;

    }

}
