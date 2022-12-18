package com.cooler.ai.platform.service.framework.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.platform.entity2.*;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.*;
import com.cooler.ai.platform.model.*;
import com.cooler.ai.platform.service2.PolicyActionService;
import com.cooler.ai.platform.service2.PolicyConditionService;
import com.cooler.ai.platform.service2.PolicyService;
import com.cooler.ai.platform.service.framework.PolicyProcessService;
import com.cooler.ai.platform.util.HttpUtil;
import com.cooler.ai.platform.util.ScriptUtil;
import com.cooler.ai.platform.util.StringUtil;
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

    @Qualifier("policyConditionService")
    @Autowired
    private PolicyConditionService policyConditionService;

    @Qualifier("policyActionService")
    @Autowired
    private PolicyActionService policyActionService;

    @Override
    public List<PolicyAction> queryPolicy(DialogState dialogState) {
        logger.debug("3.1.---------------------------查询策略（level1）");

        //1.利用dst结果进行决策策略（看看dst是否变迁成功，如果变迁不成功，则进行必须槽位检测，如果有缺失必须槽位，则进行槽位追问动作，其ID为INQUIRY_ACTION_ID）
        String domainName = dialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);
        String taskName = dialogState.getParamValue(PC.TASK_NAME, Constant.PLATFORM_PARAM);
        String intentName = dialogState.getParamValue(PC.INTENT_NAME, Constant.PLATFORM_PARAM);
        String transformIntentName = dialogState.getParamValue(PC.TRANSFORM_INTENT_NAME, Constant.PLATFORM_PARAM);
        String checkedConditionsJS = dialogState.getParamValue(PC.CHECKED_CONDITIONS, Constant.PLATFORM_PARAM);

        String fromState = dialogState.getParamValue(PC.FROM_STATE, Constant.PLATFORM_PARAM);
        String fromState2 = dialogState.getParamValue(PC.FROM_STATE2, Constant.PLATFORM_PARAM);
        String toState = dialogState.getParamValue(PC.TO_STATE, Constant.PLATFORM_PARAM);
        if (toState.equals(Constant.GLOBAL_ERROR)) {
            Integer targetPolicyId = Policy.DEFAULT_POLICY.getId();
            PolicyAction defaultAction = PolicyAction.getDefaultAction();
            dialogState.addToParamValueMap(PC.POLICY_ID, targetPolicyId + "", Constant.PLATFORM_PARAM);
            logger.info("Policy Selecting : [ {} {} ] +  ( {} {} ) -> [ {} ] --> [ P:{} ]。 ",
                    fromState,
                    fromState2.equals(fromState) ? "" : " s2:"+ fromState2,
                    intentName,
                    intentName.endsWith(transformIntentName) ? "" : " ti:" + transformIntentName,
                    toState, targetPolicyId);
            return Arrays.asList(defaultAction);       //todo:此处可以传入各个变量，得到具体针对当前场景的异常动作
        }

        //2.确认了变迁已经成功，使用组合码（stateId+intentId）查找动作策略，并进行状态迁移DST，即改变intentState
        boolean isStateBackToStart = !fromState.equals(fromState2) && fromState2.equals(Constant.GLOBAL_START);
        boolean isIntentTransformed = !transformIntentName.equals(intentName);

        String targetFromState = isStateBackToStart ? fromState2 : fromState;

        List<Policy> policies = policyService.selectByFromToState(domainName, taskName, targetFromState, transformIntentName, toState);     //先匹配转移意图，此转移意图至少是原始意图。
        Policy targetPolicy = selectPolicies(policies, checkedConditionsJS);

        if (targetPolicy == null && isIntentTransformed) {
            policies = policyService.selectByFromToState(domainName, taskName, targetFromState, intentName, toState);   //如果发生了意图转义，则原始意图和转义意图一定不一样
            targetPolicy = selectPolicies(policies, checkedConditionsJS);
        }

        if (targetPolicy != null) {
            logger.info("Policy Selecting : [ {} {} ] +  ( {} {} ) -> [ {} ] --> [ P:{} ]。 ",
                    fromState,
                    fromState2.equals(fromState) ? "" : " s2:"+ fromState2,
                    intentName,
                    intentName.endsWith(transformIntentName) ? "" : " ti:" + transformIntentName,
                    toState, targetPolicy.getId());

            logger.debug(" --> Policies: {} \n -> targetPolicy: {} ",
                    JSONObject.toJSONString(policies), JSONObject.toJSONString(targetPolicy));
        } else {
            logger.warn("Policy Error : [ {} / {}] + ( {} / t: {}) -> [ {} ] 没有获得相关策略，请联系管理员检查！ ",
                    fromState, fromState2, intentName, transformIntentName, toState);
            targetPolicy = Policy.DEFAULT_POLICY;                                                                       //如果policies没东西，则兜底策略加进去，作为保底
        }

        Integer targetPolicyId = targetPolicy.getId();
        dialogState.addToParamValueMap(PC.POLICY_ID, targetPolicyId + "", Constant.PLATFORM_PARAM);

        List<PolicyAction> actions = policyActionService.getByPolicyId(targetPolicyId);
        Collections.sort(actions);

        return actions;
    }

    /**
     * 选择目标策略
     * @param policies
     * @param checkedConditionsStr
     * @return
     */
    private Policy selectPolicies(List<Policy> policies, String checkedConditionsStr) {
        if(policies == null || policies.size() == 0) return null;
        List<ConditionNodeRecord> checkedConditions = null;
        if(checkedConditionsStr != null && checkedConditionsStr.length() > 0) {
            checkedConditions = JSON.parseArray(checkedConditionsStr, ConditionNodeRecord.class);
        } else {
            return policies.get(0);                                                                                     //如果checkedConditionStr为空，则返回第一个满足的policy即可
        }
        Map<Integer, Policy> policyMap = new HashMap<>();
        for (Policy policy : policies) {
            Integer policyId = policy.getId();
            policyMap.put(policyId, policy);
        }

        Map<Integer, List<ConditionNodeRecord>> conditionsMap = new HashMap<>();
        List<PolicyCondition> policyConditions = policyConditionService.getPolicyCondition(policyMap.keySet());
        if(policyConditions == null || policyConditions.size() == 0) return  policies.get(0);
        for (PolicyCondition policyCondition : policyConditions) {
            Integer policyId = policyCondition.getPolicyId();
            String conditionName = policyCondition.getConditionName();
            Byte conditionWhether = policyCondition.getConditionWhether();
            String whether = null;                                                                                      //如果conditionWether = 0, whether就为null
            if(conditionWhether == (byte)-1){
                whether = "N";
            } else if(conditionWhether == (byte)1 || conditionWhether == (byte)0){
                whether = "Y";
            }
            ConditionNodeRecord conditionNodeRecord = new ConditionNodeRecord(conditionName, whether);
            List<ConditionNodeRecord> conditionNodeRecordsTmp = conditionsMap.get(policyId);
            if(conditionNodeRecordsTmp == null) conditionNodeRecordsTmp = new ArrayList<>();
            conditionNodeRecordsTmp.add(conditionNodeRecord);
            conditionsMap.put(policyId, conditionNodeRecordsTmp);
        }

        for (Policy policy : policyMap.values()) {
            List<ConditionNodeRecord> conditionNodeRecordsTmp = conditionsMap.get(policy.getId());
            boolean containsAllCondition = checkedConditions.containsAll(conditionNodeRecordsTmp);
            if(containsAllCondition) return policy;
        }
        return null;
    }

    @Override
    public DMResponse runActions(DialogState dialogState, List<PolicyAction> actions) {
        Map[] fiveMaps = {
                dialogState.getFromModelStateMap(Constant.PLATFORM_PARAM_MAP, Map.class),
                dialogState.getFromModelStateMap(Constant.CUSTOM_PARAM_MAP, Map.class),
                dialogState.getFromModelStateMap(Constant.SLOT_PARAM_MAP, Map.class),
                dialogState.getFromModelStateMap(Constant.BIZ_PARAM_MAP, Map.class),     //第1个Map作为全局变量池（先初始化为DS中的BIZ_PARAM_MAP，后面可对其进行修改）
                new HashMap<String, String>()                                            //第2个Map作为局部变量池
        };
        PolicyAction interactiveAction = null;
        int i = 0, actionSize = actions.size();
        for (PolicyAction action : actions) {
            Integer actionId = action.getId();
            String actionName = action.getActionName();
            Integer actionType = action.getActionType();
            Integer policyId = action.getPolicyId();
            Integer groupNum = action.getGroupNum();
            String actionContent = action.getActionContent();                       //PROCESSED_ACTION下，为JS脚本；  HTTP_ACTION下，为Http请求所需参数的JSON字符串； INTERACTIVE_ACTION下，为话术模板；
            logger.info("Action Running   : P:{} -> A: ( {}/{} ) [ actionId: {}, actionName: {}, actionType: {} ] ", policyId, ++ i, actionSize, groupNum, actionId, actionName, actionType);

            if (actionType == Constant.PROCESSED_ACTION) {
                Map<String, Map<String, String>> newTwoMaps = ScriptUtil.runScript(actionContent, fiveMaps[0], fiveMaps[1], fiveMaps[2], fiveMaps[3], fiveMaps[4]);//带出新的全局变量和局部变量结果值，twoMaps进行更新
                if(newTwoMaps != null){
                    fiveMaps[3].putAll(newTwoMaps.get("bps"));                       //有了这一句，实际上脚本传出的newTwoMaps的gps的map已经加入到DialogState里面了，记录成了上下文可用于下一轮使用；
                    fiveMaps[4].putAll(newTwoMaps.get("lps"));                       //而这一句newTwoMaps的lps的map只加入到一个临时大Map，用完就没有了。
                }
            } else if (actionType == Constant.HTTP_ACTION) {
                Map<String, String> httpParams = HttpUtil.runHttpAction(actionContent, fiveMaps[0], fiveMaps[1], fiveMaps[2], fiveMaps[3], fiveMaps[4]);
                if(httpParams != null) {
                    fiveMaps[4].putAll(httpParams);                                  //得到的httpParams 默认 作为局部变量，如果其有部分变量需要转为全局变量，则加PROCESSED_ACTION的脚本进行变量转移
                }
            } else if (actionType == Constant.INTERACTIVE_ACTION) {                 //交互动作限定一个Policy只有一个，找出来最后执行
                interactiveAction = action;
            }
        }

        String interactiveData = null;
        if(interactiveAction == null){
            String policyId = dialogState.getParamValue(PC.POLICY_ID, Constant.PLATFORM_PARAM);
            logger.error("Action Error     : policyId为 {} 的策略没有获得相关的交互动作，请联系管理员检查！ ",  policyId);
            interactiveData = "抱歉！出现系统错误，请重新试一次吧！(1)";
        } else {
            interactiveData = interactiveAction.getActionContent();
            interactiveData = StringUtil.replaceVariableValues(interactiveData, fiveMaps[0], fiveMaps[1], fiveMaps[2], fiveMaps[3], fiveMaps[4]);    //都会用来构建交互数据，但twoMaps[0]会保存到下一轮，twoMaps[1]用完会丢弃
        }

        logger.info("END              : [ MAC ] : {} \n", interactiveData);
        DMResponse dmResponse = runInteractiveAction(dialogState, interactiveData);
        return dmResponse;
    }


    private DMResponse runInteractiveAction(DialogState dialogState, String actionContent) {
        Message message = new Message();
        message.setMessageType(Constant.MSG_TEXT);
        message.setMessageData(actionContent);

        DMResponse dmResponse = new DMResponse(0, "success", dialogState.getBotName(), dialogState.getSessionId(), dialogState.getTotalTurnNum(), Arrays.asList(message), null, null, 0l, 0l);
        return dmResponse;
    }

}
