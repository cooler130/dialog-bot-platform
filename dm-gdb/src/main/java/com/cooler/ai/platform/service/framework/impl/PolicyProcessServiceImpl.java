package com.cooler.ai.platform.service.framework.impl;

import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.platform.entity2.*;
import com.cooler.ai.platform.entity2.Action;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.*;
import com.cooler.ai.platform.model.*;
import com.cooler.ai.platform.service2.ActionService;
import com.cooler.ai.platform.service2.ConditionKVService;
import com.cooler.ai.platform.service2.PolicyService;
import com.cooler.ai.platform.service.framework.PolicyProcessService;
import com.cooler.ai.platform.util.ActionScript;
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
    public List<Action> queryPolicy(DialogState dialogState) {
        logger.debug("3.1.---------------------------查询策略（level1）");

        //1.利用dst结果进行决策策略（看看dst是否变迁成功，如果变迁不成功，则进行必须槽位检测，如果有缺失必须槽位，则进行槽位追问动作，其ID为INQUIRY_ACTION_ID）
        String domainName = dialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);
        String taskName = dialogState.getParamValue(PC.TASK_NAME, Constant.PLATFORM_PARAM);
        String intentName = dialogState.getParamValue(PC.INTENT_NAME, Constant.PLATFORM_PARAM);
        String transformIntentName = dialogState.getParamValue(PC.TRANSFORM_INTENT_NAME, Constant.PLATFORM_PARAM);
        String fromState = dialogState.getParamValue(PC.FROM_STATE, Constant.PLATFORM_PARAM);
        String fromState2 = dialogState.getParamValue(PC.FROM_STATE2, Constant.PLATFORM_PARAM);
        String toState = dialogState.getParamValue(PC.TO_STATE, Constant.PLATFORM_PARAM);
        if (toState.equals(EntityConstant.ERROR_STATE)) {
            return Arrays.asList(Action.getDefaultAction());       //todo:此处可以传入各个变量，得到具体针对当前场景的异常动作
        }

//        String selectedTransitions = dialogState.getParamValue(PC.SELECTED_TRANSITIONS, Constant.PLATFORM_PARAM);
//        String firstBadConditionDataJS = dialogState.getParamValue(PC.FIRST_BAD_CONDITION_DATA, Constant.PLATFORM_PARAM);
//        BaseConditionData firstBadConditionData = JSON.parseObject(firstBadConditionDataJS, BaseConditionData.class);
//        if(selectedTransitions == null){                                                                                //如果没有"selectedTransitions"，说明dst阶段状态维持原始状态，没有变迁成功
//            if(firstBadConditionData != null){                                                                          //  1.没有变迁成功，但还是找到了失败原因
//                ConditionLogic conditionLogic = firstBadConditionData.getConditionLogic();
//                byte noPassNotice = 0;
//                if(conditionLogic != null){
//                    noPassNotice = conditionLogic.getNopassNotice();
//                }
//                if(noPassNotice == (byte)1) return Action.getInquiryAction();                                           //  只有firstBadConditionData有值，又需要提示/询问（noPassNotice == 1），才去提示/询问，前面的逻辑，有需要提示/询问，则肯定优先返回的，见ConditionContext.findFirstBadConditionData()。
//            }else{                                                                                                      //  2.没有变迁成功，还没找到失败原因，则兜底
//                return Action.getDefaultAction();
//            }
//        }

        //2.确认了变迁已经成功，使用组合码（stateId+intentId）查找动作策略，并进行状态迁移DST，即改变intentState
        boolean isStateBackToStart = !fromState.equals(fromState2) && fromState2.equals(Constant.GLOBAL_START);
        boolean isIntentTransformed = !transformIntentName.equals(intentName);

        String targetFromState = isStateBackToStart ? fromState2 : fromState;

        List<Policy> policies = policyService.selectByFromToState(domainName, taskName, targetFromState, transformIntentName, toState);     //先匹配转移意图，此转移意图至少是原始意图。
        Policy targetPolicy = checkPolicies(policies, dialogState);

        if (targetPolicy == null && isIntentTransformed) {
            policies = policyService.selectByFromToState(domainName, taskName, targetFromState, intentName, toState);   //如果发生了意图转义，则原始意图和转义意图一定不一样
            targetPolicy = checkPolicies(policies, dialogState);
        }

        if (targetPolicy != null) {
            logger.debug("3.1.经过选择，domainName: {}, taskName: {}, intentName: {}, transformIntentName: {}, fromState: {}, fromState2: {}, toState: {} \n --> Policies: {} \n -> targetPolicy: {} ",
                    domainName, taskName, intentName, transformIntentName, fromState, fromState2, toState, JSONObject.toJSONString(policies), JSONObject.toJSONString(targetPolicy));
        } else {
            logger.debug("3.2.经过选择，domainName: {}, taskName: {}, intentName: {}, transformIntentName: {}, fromState: {}, fromState2: {}, toState: {} 没有获得相关策略，请联系管理员检查！ ",
                    domainName, taskName, intentName, transformIntentName, fromState, fromState2, toState);
            targetPolicy = Policy.DEFAULT_POLICY;                                                                       //如果policies没东西，则兜底策略加进去，作为保底
        }

        Integer targetPolicyId = targetPolicy.getId();
        dialogState.addToParamValueMap(PC.POLICY_ID, targetPolicyId + "", Constant.PLATFORM_PARAM);

        List<Action> actions = actionService.getByPolicyId(targetPolicyId);
        Collections.sort(actions);

        return actions;
    }

    private Policy checkPolicies(List<Policy> policies, DialogState dialogState) {
        if (policies == null || policies.size() == 0) return null;
        Map<Integer, Policy> policyMap = new HashMap<>();
        for (Policy policy : policies) {
            Integer policyId = policy.getId();
            policyMap.put(policyId, policy);
        }
        Map<Integer, List<ConditionKV>> conditionKVMap = new HashMap<>();
        List<ConditionKV> conditionKVs = conditionKVService.getConditionKVsByPolicyIds(policyMap.keySet());
        if (conditionKVs != null) {                                                                                       //如果校验条件集合不为空，则需要先分组：Map<policyId, List<ConditionKV>>
            for (ConditionKV conditionKV : conditionKVs) {
                Integer policyId = conditionKV.getPolicyId();
                List<ConditionKV> conditionKVGroup = conditionKVMap.get(policyId);
                if (conditionKVGroup == null) {
                    conditionKVGroup = new ArrayList<>();
                }
                conditionKVGroup.add(conditionKV);
                conditionKVMap.put(policyId, conditionKVGroup);
            }

            for (Integer policyId : policyMap.keySet()) {
                List<ConditionKV> conditionKVGroup = conditionKVMap.get(policyId);
                if (conditionKVGroup == null) {
                    return policyMap.get(policyId);
                } else {
                    boolean currentPolicyResult = getPolicyConditionKV(conditionKVGroup, dialogState);                  //此时经过slotOperate和dst，槽位和组合数据都是完备的。
                    if (currentPolicyResult) {
                        return policyMap.get(policyId);
                    }
                }
            }
        }
        return null;
    }

    private boolean getPolicyConditionKV(List<ConditionKV> conditionKVs, DialogState dialogState) {
        //1.对conditionKV集合进行分组（组内为或、组间为并）
        Map<Integer, List<ConditionKV>> conditionKVGroupMap = new HashMap<>();
        for (ConditionKV conditionKV : conditionKVs) {
            Integer groupNum = conditionKV.getGroupNum();
            List<ConditionKV> conditionKVGroupItem = conditionKVGroupMap.get(groupNum);
            if (conditionKVGroupItem == null) {
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
                if (slotValue != null) {
                    switch (relationship) {
                        case Constant.R_EQUAL: {
                            if (slotValue.equals(conditionValue)) itemResult = true;
                            break;
                        }
                        case Constant.R_NO_EQUAL: {
                            if (!slotValue.equals(conditionValue)) itemResult = true;
                            break;
                        }
                        case Constant.R_MORE: {
                            try {
                                int businessValueIntValue = Integer.parseInt(slotValue);
                                int conditionValueIntValue = Integer.parseInt(conditionValue);
                                if (businessValueIntValue > conditionValueIntValue) itemResult = true;
                            } catch (Exception e) {
                                logger.error("ConditionKV设置错误（比较大小，却不是数值）！其Id号为：{}", conditionKV.getId());
                            }
                            break;
                        }
                        case Constant.R_MORE_EQUAL: {
                            try {
                                int businessValueIntValue = Integer.parseInt(slotValue);
                                int conditionValueIntValue = Integer.parseInt(conditionValue);
                                if (businessValueIntValue >= conditionValueIntValue) itemResult = true;
                            } catch (Exception e) {
                                logger.error("ConditionKV设置错误（比较大小，却不是数值）！其Id号为：{}", conditionKV.getId());
                            }
                            break;
                        }
                        case Constant.R_LESS: {
                            try {
                                int businessValueIntValue = Integer.parseInt(slotValue);
                                int conditionValueIntValue = Integer.parseInt(conditionValue);
                                if (businessValueIntValue < conditionValueIntValue) itemResult = true;
                            } catch (Exception e) {
                                logger.error("ConditionKV设置错误（比较大小，却不是数值）！其Id号为：{}", conditionKV.getId());
                            }
                            break;
                        }
                        case Constant.R_LESS_EQUAL: {
                            try {
                                int businessValueIntValue = Integer.parseInt(slotValue);
                                int conditionValueIntValue = Integer.parseInt(conditionValue);
                                if (businessValueIntValue <= conditionValueIntValue) itemResult = true;
                            } catch (Exception e) {
                                logger.error("ConditionKV设置错误（比较大小，却不是数值）！其Id号为：{}", conditionKV.getId());
                            }
                            break;
                        }
                        case Constant.R_CONTAIN: {
                            if (slotValue.contains(conditionValue)) itemResult = true;
                            break;
                        }
                        case Constant.R_NOT_CONTAIN: {
                            if (!slotValue.contains(conditionValue)) itemResult = true;
                            break;
                        }
                        case Constant.R_BE_CONTAINED: {
                            String[] conditionValueItems = conditionValue.split(",");
                            for (String conditionValueItem : conditionValueItems) {
                                if (conditionValueItem.equals(slotValue)) {
                                    itemResult = true;
                                    break;
                                }
                            }
                            break;
                        }
                        case Constant.R_NOT_BE_CONTAINED: {
                            itemResult = true;
                            String[] conditionValueItems = conditionValue.split(",");
                            for (String conditionValueItem : conditionValueItems) {
                                if (conditionValueItem.equals(slotValue)) {
                                    itemResult = false;
                                    break;
                                }
                            }
                            break;
                        }
                        case Constant.R_MATCH_REGEXP: {
                            if (slotValue.matches(conditionValue)) itemResult = true;
                            break;
                        }
                        case Constant.R_NOT_MATCH_REGEXP: {
                            if (!slotValue.matches(conditionValue)) itemResult = true;
                            break;
                        }
                    }
                } else {
                    if (relationship == Constant.R_NONE) {
                        itemResult = true;
                    }
                }

                //获取上面的itemResult，再进行下面的逻辑运算
                int logicType = (int) conditionKV.getLogicType();                                                            //1：并；  0：或； -1：非
                switch (logicType) {
                    case Constant.AND: {
                        sb.append(groupResult).append(" && ").append(itemResult);
                        groupResult = groupResult && itemResult;
                        break;
                    }
                    case Constant.OR: {
                        sb.append(groupResult).append(" || ").append(itemResult);
                        groupResult = groupResult || itemResult;
                        break;
                    }
                    case Constant.NOT: {
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
    public DMResponse runActions(DMRequest dmRequest, DialogState dialogState, List<Action> actions, Map<String, BizDataModelState<String>> bizDataMap) {
        Action interactiveAction = null;
        for (Action action : actions) {
            Integer actionId = action.getId();
            String actionName = action.getActionName();
            Integer actionType = action.getActionType();
            String actionContent = action.getActionContent();
            logger.debug("start to run action, actionId: {}, actionName: {}, actionType：{}", actionId, actionType, actionName);


            if (actionType == Constant.PROCESSED_ACTION) {
                runProcessAction(dmRequest, dialogState, bizDataMap, actionContent);
            } else if (actionType == Constant.HTTP_ACTION) {
                runHttpAction(dmRequest, dialogState, bizDataMap, actionContent);
            } else if (actionType == Constant.INTERACTIVE_ACTION) {             //交互动作限定一个Policy只有一个，找出来最后执行
                interactiveAction = action;
            }
        }

        DMResponse dmResponse = runInteractiveAction(dmRequest, dialogState, bizDataMap, interactiveAction.getActionContent());
        return dmResponse;
    }

    private void runProcessAction(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMap, String actionContent) {
        if(actionContent == null || actionContent.length() == 0) return;
//        String[] scriptLines = actionContent.split(";");
//        for (String scriptLine : scriptLines) {
//            if(scriptLine.startsWith("#")) continue;
//            String code = "\t\tSystem.out.println(\"hello world\");\n";
            ActionScript.runScript(actionContent);

//        }
    }

    private void runHttpAction(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMap, String actionContent) {

    }

    private DMResponse runInteractiveAction(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMap, String actionContent) {
        Message message = new Message();
        message.setMessageType(Constant.MSG_TEXT);
        message.setMessageData(actionContent);

        DMResponse dmResponse = new DMResponse(0, "fail", dialogState.getBotName(), dmRequest.getSessionId(), dialogState.getTotalTurnNum(), Arrays.asList(message), null, null, 0l, 0l);
        return dmResponse;
    }

}
