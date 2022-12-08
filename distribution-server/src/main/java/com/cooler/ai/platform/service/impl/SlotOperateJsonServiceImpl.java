//package com.cooler.ai.platform.service.impl;
//
//import com.cooler.ai.platform.facade.constance.CC;
//import com.cooler.ai.platform.facade.constance.Constant;
//import com.cooler.ai.platform.facade.constance.PC;
//import com.cooler.ai.platform.facade.model.*;
//import com.cooler.ai.platform.entity.Intent;
//import com.cooler.ai.platform.service.*;
//import com.cooler.ai.platform.strategy.LanguageStrategy;
//import com.cooler.ai.platform.strategy.NonLanguageStrategy;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service("slotOperateJsonService")
//public class SlotOperateJsonServiceImpl implements SlotOperateService {
//
//    private Logger logger = LoggerFactory.getLogger(SlotOperateJsonServiceImpl.class);
//
//    @Autowired
//    private NonLanguageStrategy nonLanguageStrategy;
//
//    @Autowired
//    private LanguageStrategy languageStrategy;
//
//    @Qualifier("jsonNLUIntentService")
//    @Autowired
//    private NLUIntentService nluIntentService;
//
//    @Qualifier("jsonSlotService")
//    @Autowired
//    private SlotService slotService;
//
//    @Qualifier("jsonSlotRelationService")
//    @Autowired
//    private SlotRelationService slotRelationService;
//
//    @Qualifier("jsonNLUSlotService")
//    @Autowired
//    private NLUSlotService nluSlotService;
//
//    @Qualifier("jsonIntentService")
//    @Autowired
//    private IntentService intentService;
//
//    @Autowired
//    private CacheService<DomainTaskData> turnNumCacheService;
//
//    @Override
//    public DialogState operationSlots(DMRequest dmRequest, List<DialogState> historyDialogStates, Map<String, BizDataModelState<String>> bizDataMSMap) {
//        //1.准备好各个参数
//        String sessionId = dmRequest.getSessionId();
//        DomainTaskData domainTaskData = turnNumCacheService.getContext(sessionId + "_" + Constant.DOMAIN_TASK_DATA);
//        if(domainTaskData == null) domainTaskData = new DomainTaskData(sessionId, 0, new HashMap<>());
//
//        String sentence = null;                                                                                         //原句
//        String requestType = dmRequest.getRequestType();                                                                //本轮请求类型（语音请求、点击动作请求）
//
//        String nluDomainName = null;                                                                                    //NLU领域名
//        String nluIntentName = null;                                                                                    //NLU意图名
//
//        String domainName = null;                                                                                       //DM领域名
//        String taskName = null;                                                                                         //任务名
//        String intentName = null;                                                                                       //DM意图名
//
//        Intent targetIntent = null;                                                                                     //目标意图
//        Integer targetIntentId = null;                                                                                  //目标意图ID（DM意图的ID）
//        Integer targetLastFromStateId = null;                                                                           //前面一轮的fromStateId
//        Integer targetFromStateId = null;                                                                               //前面一轮的toStateId，可以作为本轮的fromStateId
//
//        Boolean finalSameDomain = null;                                                                                 //是否跟上轮相同领域
//
//        Map<String, SlotState> targetSlotStateMap = null;                                                               //目标槽位集合（由语言交互提供）
//        Map<String, String> targetParamValueMap = null;                                                                 //目标槽位值集合（由动作交互提供）
//
//        Map<String, SlotState> unkonwSlotStateMap = null;                                                               //目标未知槽位集合，就是没有解析到本意图的槽位（由语言交互提供）
//
//
//        //2.准备好上一轮所用到的 NLU_DOMAIN_NAME 和 DM_DOMAIN
//        DomainDesionData domainDesionData = null;
//        Map<String, String> domainDecisionDataMap = new HashMap<>();
//
//        //3.1.通过槽位填充和计算，确定NLU解析后的目标领域和目标意图（先填槽补槽，才能在多个 (领域，意图）对里面决策出一个最佳选择
//        if(Constant.LANGUAGE_QUERYTYPES.contains(requestType)){
//            domainDesionData = languageStrategy.decide(dmRequest, historyDialogStates, domainTaskData, nluIntentService, nluSlotService, intentService, slotService, slotRelationService, domainDecisionDataMap);
//        }
//        //3.2.确定点击事件渠道的目标领域和目标意图（这里的点击事件需要明确domainName、intentId和stateId，后续需要注意传递这些值）
//        else if (Constant.NON_LANGUAGE_QUERYTYPES.contains(requestType)) {
//            domainDesionData = nonLanguageStrategy.decide(dmRequest, historyDialogStates, domainTaskData, intentService);
//        } else {
//            //todo:抛出传值错误异常
//        }
//
//        sentence = domainDesionData.getSentence();
//
//        nluDomainName = domainDesionData.getNluDomainName();
//        nluIntentName = domainDesionData.getNluIntentName();
//        domainName = domainDesionData.getDomainName();
//        intentName = domainDesionData.getIntentName();
//
//        targetIntentId = domainDesionData.getIntentId();
//        taskName = domainDesionData.getTaskName();
//        targetLastFromStateId = domainDesionData.getLastFromStateId();
//        targetFromStateId = domainDesionData.getFromStateId();
//
//        targetSlotStateMap = domainDesionData.getFixedSlotStateMap();
//        unkonwSlotStateMap = domainDesionData.getUnknownSlotStateMap();
//        targetParamValueMap = domainDesionData.getFixedParamValueMap();
//        finalSameDomain = domainDesionData.getSameDomain();
//
//        targetIntent = intentService.selectByIntentId(targetIntentId);
//
//        //5.DialogState装载各类数据（包含槽位数据和系统数据）
//        DialogState dialogState = new DialogState();
//
//        dialogState.setSessionId(dmRequest.getSessionId());
//        ClientInfo clientInfo = dmRequest.getClientInfo();
//        String channel = clientInfo.getChannel();
//        UserInfo userInfo = dmRequest.getUserInfo();
//        dialogState.setClientId(clientInfo != null ? clientInfo.getClientId() : null);
//        dialogState.setUserId(userInfo != null ? userInfo.getUserId() : null);
//
//        dialogState.setInteractiveData(sentence);
//
//        dialogState.setDomainName(domainName);
//        dialogState.setTaskName(taskName);
//        dialogState.setIntentName(intentName);
//        //todo：此botName应该通过domainName以及其他参数（clientId、userId、channel等）来获取，前期先使用domainName一个，在DistributionCenterFacadeImpl调用xmlDMFacadeMapService，这个Service中已经建立好了bot和Facade的映射关系，以后可以优化
//        String botName = domainName + "Bot";
//        dialogState.setBotName(botName);
//
//        domainTaskData.increaseTurnNum(domainName, taskName);                //明确了domainName和taskName后，就要针对各个turnNum加一
//        dialogState.setTotalTurnNum(domainTaskData.getTotalTurnNum());
//        dialogState.setDomainTurnNum(domainTaskData.getDomainTurnNum(domainName));
//        dialogState.setDomainTaskTurnNum(domainTaskData.getTaskTurnNum(domainName, taskName));
//
//        dialogState.addToModelStateMap(Constant.SO_DOMAIN_DECISION_MAP, domainDecisionDataMap);
//
//        fillParamData(dialogState, dmRequest, sentence, nluDomainName, targetIntent, targetSlotStateMap, unkonwSlotStateMap, targetParamValueMap, finalSameDomain, targetLastFromStateId, targetFromStateId, bizDataMSMap);
//
//        return dialogState;
//    }
//
//    /**
//     * 将系统槽位和业务槽位加载到dialogState中的PARAM_VALUE_MAP中，将业务槽位原始值放到SLOT_STATE_MAP中，将未知槽位原始值放到UNKNOWN_SLOT_STATE_MAP中
//     * dialogState后面只会有SLOT_STATE_MAP、UNKNOWN_SLOT_STATE_MAP和PARAM_VALUE_MAP，重点使用PARAM_VALUE_MAP（它又包括系统值、业务值，以及后面加入的合成值）
//     *
//     * @param dialogState
//     * @param targetNluDomainName
//     * @param targetIntent
//     * @param targetSlotStateMap
//     * @param targetUnkonwSlotStateMap
//     * @param sameDomain
//     * @param lastFromStateId
//     * @param fromStateId
//     */
//    private void fillParamData(DialogState dialogState, DMRequest dmRequest, String sentence, String targetNluDomainName, Intent targetIntent,
//                               Map<String, SlotState> targetSlotStateMap, Map<String, SlotState> targetUnkonwSlotStateMap, Map<String, String> targetSlotValueMap,
//                               Boolean sameDomain, int lastFromStateId, int fromStateId, Map<String, BizDataModelState<String>> bizDataMSMap) {
//
//        //保存本轮的Intent，以及意图相关数据（NLU_DOMAIN_NAME、DM_DOMAIN， 这两个领域可能能用到这轮业务计算）
//        Map<String, String> paramValueMap = new HashMap<>();
//        paramValueMap.put("$" + PC.SENTENCE + "$", sentence);
//        paramValueMap.put("$" + PC.SAME_DOMAIN + "$", sameDomain + "");
////        paramValueMap.put("$" + PC.LAST_FROM_STATE_ID + "$", lastFromStateId + "");
////        paramValueMap.put("$" + PC.FROM_STATE_ID + "$", fromStateId + "");
//
//        paramValueMap.put("$" + PC.NLU_DOMAIN_NAME + "$", targetNluDomainName);
//
//        paramValueMap.put("$" + PC.DOMAIN_NAME + "$", targetIntent.getDomainName());
//        paramValueMap.put("$" + PC.INTENT_TYPE + "$", targetIntent.getIntentType() + "");
//        paramValueMap.put("$" + PC.INTENT_ID + "$", targetIntent.getId() + "");
//        paramValueMap.put("$" + PC.INTENT_NAME + "$", targetIntent.getIntentName());
//
//        paramValueMap.put("$" + PC.TASK_NAME + "$", dialogState.getTaskName() + "");
//
//        //保存得到的槽位数据，包括意图相关槽位和未识别槽位
//        Map<String, SlotState> allSlotStateMap = new HashMap<>();
//        if(targetSlotStateMap != null && targetSlotStateMap.size() > 0){
//            dialogState.addToModelStateMap(Constant.SLOT_STATE_MAP, targetSlotStateMap);
//            allSlotStateMap.putAll(targetSlotStateMap);
//        }
//        if (targetUnkonwSlotStateMap != null && targetUnkonwSlotStateMap.size() > 0) {
//            dialogState.addToModelStateMap(Constant.UNKNOWN_SLOT_STATE_MAP, targetUnkonwSlotStateMap);
//            allSlotStateMap.putAll(targetUnkonwSlotStateMap);
//        }
//
//        for (String slotName : allSlotStateMap.keySet()) {
//            SlotState slotState = allSlotStateMap.get(slotName);
//            List<SlotInfo> slotInfos = slotState.getSlotInfos();
//            if(slotInfos != null && slotInfos.size() > 0){
//                SlotInfo slotInfo = slotInfos.get(0);
//                String value = slotInfo.getValue();
//                paramValueMap.put(slotName, value);
//            }
//        }
//
//        if(targetSlotValueMap != null && targetSlotValueMap.size() > 0){
//            for (String slotName : targetSlotValueMap.keySet()) {
//                String slotValue = targetSlotValueMap.get(slotName);
//                paramValueMap.put(slotName, slotValue);
//            }
//        }
//
//        //将DMRequest里面的数据作为定制参数加入到paramValueMap中
//        paramValueMap.put("#" + CC.SESSION_ID + "#", dmRequest.getSessionId());
//        paramValueMap.put("#" + CC.TURN_NUM + "#", dialogState.getTotalTurnNum() + "");         //这个数据前面已经设置到了dialogState里面了
//        paramValueMap.put("#" + CC.QUERY_TYPE + "#", dmRequest.getRequestType());
//        ClientInfo clientInfo = dmRequest.getClientInfo();
//        if(clientInfo != null){
//            paramValueMap.put("#" + CC.CLIENT_ID + "#", clientInfo.getClientId());
//            paramValueMap.put("#" + CC.CHANNEL + "#", clientInfo.getChannel());
//            paramValueMap.put("#" + CC.CLIENT_NAME + "#", clientInfo.getClientName());
//            paramValueMap.put("#" + CC.CLIENT_TYPE + "#", clientInfo.getClientType());
//        }
//        UserInfo userInfo = dmRequest.getUserInfo();
//        if(userInfo != null){
//            paramValueMap.put("#" + CC.USER_ID + "#", userInfo.getUserId());
//            paramValueMap.put("#" + CC.USER_NAME + "#", userInfo.getUserName());
//        }
//        LocationInfo locationInfo = dmRequest.getLocationInfo();
//        if(locationInfo != null){
//            paramValueMap.put("#" + CC.CITY_NAME + "#", locationInfo.getCityName());
//        }
//        Map<String, String> extendInfo = dmRequest.getExtendInfo();
//        if(extendInfo != null && extendInfo.size() > 0){
//            for (String key : extendInfo.keySet()) {
//                paramValueMap.put("#" + key + "#", extendInfo.get(key));
//            }
//        }
//
//        //将前面轮次得到的所有业务参数，全部加入到paramValueMap中
//        if(bizDataMSMap != null && bizDataMSMap.size() > 0){
//            for (String bizItemKey : bizDataMSMap.keySet()) {
//                BizDataModelState<String> bizItemValueMS = bizDataMSMap.get(bizItemKey);
//                String bizItemValue = bizItemValueMS.getT();
//                paramValueMap.put("%" + bizItemKey + "%", bizItemValue);
//            }
//        }
//
//        dialogState.addToModelStateMap(Constant.PARAM_VALUE_MAP, paramValueMap);
//    }
//}
