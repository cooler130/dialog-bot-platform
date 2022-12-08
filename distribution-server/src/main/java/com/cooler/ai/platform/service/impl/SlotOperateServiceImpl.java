package com.cooler.ai.platform.service.impl;

import com.cooler.ai.nlu.SlotInfo;
import com.cooler.ai.platform.EntityConstant;
import com.cooler.ai.platform.entity.Intent;
import com.cooler.ai.platform.facade.constance.CC;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.*;
import com.cooler.ai.platform.model.DomainDesionData;
import com.cooler.ai.platform.model.DomainTaskData;
import com.cooler.ai.platform.model.SlotState;
import com.cooler.ai.platform.service.*;
import com.cooler.ai.platform.strategy.LanguageStrategy;
import com.cooler.ai.platform.strategy.NonLanguageStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("slotOperateService")
public class SlotOperateServiceImpl implements SlotOperateService {

    private Logger logger = LoggerFactory.getLogger(SlotOperateServiceImpl.class);

    @Autowired
    private NonLanguageStrategy nonLanguageStrategy;

    @Autowired
    private LanguageStrategy languageStrategy;

    @Qualifier("nluIntentService")
    @Autowired
    private NLUIntentService nluIntentService;

    @Qualifier("slotService")
    @Autowired
    private SlotService slotService;

    @Qualifier("slotRelationService")
    @Autowired
    private SlotRelationService slotRelationService;

    @Qualifier("nluSlotService")
    @Autowired
    private NLUSlotService nluSlotService;

    @Qualifier("intentService")
    @Autowired
    private IntentService intentService;

    @Autowired
    private CacheService<DomainTaskData> turnNumCacheService;


    @Override
    public DialogState operationSlots(DMRequest dmRequest, List<DialogState> historyDialogStates, Map<String, BizDataModelState<String>> bizDataMSMap) {
        //1.准备好各个参数
        String sentence = null;                                                                                         //原句
        String nluDomainName = null;                                                                                    //NLU领域名
        String nluIntentName = null;                                                                                    //NLU意图名
        String domainName = null;                                                                                       //DM领域名
        String taskName = null;                                                                                         //任务名
        String intentName = null;                                                                                       //DM意图名

        String targetLastFromState = null;
        String targetFromState = null;
        Boolean finalSameDomain = null;                                                                                 //是否跟上轮相同领域

        Map<String, SlotState> targetSlotStateMap = null;                                                               //目标槽位集合（由语言交互提供）
        Map<String, String> targetParamValueMap = null;                                                                 //目标槽位值集合（由动作交互提供）
        Map<String, SlotState> unkonwSlotStateMap = null;                                                               //目标未知槽位集合，就是没有解析到本意图的槽位（由语言交互提供）

        Integer targetIntentId = null;                                                                                  //目标意图ID（DM意图的ID）
        Intent targetIntent = null;                                                                                     //目标意图

        //2.准备好上一轮所用到的 NLU_DOMAIN_NAME 和 DM_DOMAIN
        DomainDesionData domainDesionData = null;
        Map<String, String> domainDecisionDataMap = new HashMap<>();

        //3.1.通过槽位填充和计算，确定NLU解析后的目标领域和目标意图（先填槽补槽，才能在多个 (领域，意图）对里面决策出一个最佳选择
        String sessionId = dmRequest.getSessionId();
        DomainTaskData domainTaskData = turnNumCacheService.getContext(sessionId + "_" + Constant.DOMAIN_TASK_DATA);
        if(domainTaskData == null) domainTaskData = new DomainTaskData(sessionId, 0, new HashMap<>());
        String requestType = dmRequest.getRequestType();                                                                //本轮请求类型（语音请求、点击动作请求）
        if(EntityConstant.LANGUAGE_QUERYTYPES.contains(requestType)){
            domainDesionData = languageStrategy.decide(dmRequest, historyDialogStates, domainTaskData, nluIntentService, nluSlotService, intentService, slotService, slotRelationService, domainDecisionDataMap);
        }
        //3.2.确定点击事件渠道的目标领域和目标意图（这里的点击事件需要明确domainName、intentId和stateId，后续需要注意传递这些值）
        else if (EntityConstant.NON_LANGUAGE_QUERYTYPES.contains(requestType)) {
            domainDesionData = nonLanguageStrategy.decide(dmRequest, historyDialogStates, domainTaskData, intentService);
        } else {
            //todo:抛出传值错误异常
        }

        if(domainDesionData == null){
            //todo:处理异常，抛出传值错误异常
        }

        sentence = domainDesionData.getSentence();
        nluDomainName = domainDesionData.getNluDomainName();
        nluIntentName = domainDesionData.getNluIntentName();
        domainName = domainDesionData.getDomainName();
        taskName = domainDesionData.getTaskName();
        intentName = domainDesionData.getIntentName();

        targetLastFromState = domainDesionData.getLastFromState();
        targetFromState = domainDesionData.getFromState();
        finalSameDomain = domainDesionData.getSameDomain();

        targetSlotStateMap = domainDesionData.getFixedSlotStateMap();
        targetParamValueMap = domainDesionData.getFixedParamValueMap();
        unkonwSlotStateMap = domainDesionData.getUnknownSlotStateMap();

        targetIntentId = domainDesionData.getIntentId();
        targetIntent = intentService.selectByIntentId(targetIntentId);

        //5.DialogState装载各类数据（包含槽位数据和系统数据）
        DialogState dialogState = new DialogState();

        dialogState.setSessionId(sessionId);

        ClientInfo clientInfo = dmRequest.getClientInfo();
        String channel = clientInfo.getChannel();

        UserInfo userInfo = dmRequest.getUserInfo();
        dialogState.setClientId(clientInfo.getClientId());
        dialogState.setUserId(userInfo.getUserId());

        dialogState.setInteractiveData(sentence);

        dialogState.setDomainName(domainName);
        dialogState.setTaskName(taskName);
        dialogState.setIntentName(intentName);
        //todo：此botName应该通过domainName以及其他参数（clientId、userId、channel等）来获取，前期先使用domainName一个，在DistributionCenterFacadeImpl调用xmlDMFacadeMapService，这个Service中已经建立好了bot和Facade的映射关系，以后可以优化
        dialogState.setBotName(domainName + "Bot");

        domainTaskData.increaseTurnNum(domainName, taskName);                //明确了domainName和taskName后，就要针对各个turnNum加一
        turnNumCacheService.setContext(sessionId + "_" + Constant.DOMAIN_TASK_DATA, domainTaskData);        //上面修改后，要保存的，就不要放到最后保存了
        dialogState.setTotalTurnNum(domainTaskData.getTotalTurnNum());
        dialogState.setDomainTurnNum(domainTaskData.getDomainTurnNum(domainName));
        dialogState.setDomainTaskTurnNum(domainTaskData.getTaskTurnNum(domainName, taskName));

        dialogState.addToModelStateMap(Constant.SO_DOMAIN_DECISION_MAP, domainDecisionDataMap);

        fillParamData(dialogState, dmRequest, sentence, nluDomainName, nluIntentName, targetIntent, targetSlotStateMap, unkonwSlotStateMap, targetParamValueMap, finalSameDomain, targetLastFromState, targetFromState, bizDataMSMap);

        return dialogState;
    }

    /**
     * 将系统槽位和业务槽位加载到dialogState中的PARAM_VALUE_MAP中，将业务槽位原始值放到SLOT_STATE_MAP中，将未知槽位原始值放到UNKNOWN_SLOT_STATE_MAP中
     * dialogState后面只会有SLOT_STATE_MAP、UNKNOWN_SLOT_STATE_MAP和PARAM_VALUE_MAP，重点使用PARAM_VALUE_MAP（它又包括系统值、业务值，以及后面加入的合成值）
     *
     * @param dialogState
     * @param targetNluDomainName
     * @param targetIntent
     * @param targetSlotStateMap
     * @param targetUnkonwSlotStateMap
     * @param sameDomain
     * @param lastFromState
     * @param fromState
     */
    private void fillParamData(DialogState dialogState, DMRequest dmRequest, String sentence, String targetNluDomainName, String targetNluIntentName, Intent targetIntent,
                               Map<String, SlotState> targetSlotStateMap, Map<String, SlotState> targetUnkonwSlotStateMap, Map<String, String> targetParamValueMap,
                               Boolean sameDomain, String lastFromState, String fromState, Map<String, BizDataModelState<String>> bizDataMSMap) {

        //保存本轮的Intent，以及意图相关数据（NLU_DOMAIN_NAME、DM_DOMAIN， 这两个领域可能能用到这轮业务计算）
        Map<String, String> paramValueMap = new HashMap<>();
        paramValueMap.put("$" + PC.SENTENCE + "$", sentence);
        paramValueMap.put("$" + PC.SAME_DOMAIN + "$", sameDomain + "");
        paramValueMap.put("$" + PC.LAST_FROM_STATE + "$", lastFromState + "");
        paramValueMap.put("$" + PC.FROM_STATE + "$", fromState + "");

        paramValueMap.put("$" + PC.NLU_DOMAIN_NAME + "$", targetNluDomainName);
        paramValueMap.put("$" + PC.NLU_INTENT_NAME + "$", targetNluIntentName);

        paramValueMap.put("$" + PC.DOMAIN_NAME + "$", targetIntent.getDomainName());
        paramValueMap.put("$" + PC.INTENT_TYPE + "$", targetIntent.getIntentType() + "");
        paramValueMap.put("$" + PC.INTENT_ID + "$", targetIntent.getId() + "");
        paramValueMap.put("$" + PC.INTENT_NAME + "$", targetIntent.getIntentName());

        paramValueMap.put("$" + PC.TASK_NAME + "$", dialogState.getTaskName() + "");

        //保存得到的语言解析槽位数据，包括意图相关槽位和未识别槽位
        Map<String, SlotState> allSlotStateMap = new HashMap<>();
        if(targetSlotStateMap != null && targetSlotStateMap.size() > 0){
            dialogState.addToModelStateMap(Constant.SLOT_STATE_MAP, targetSlotStateMap);
            allSlotStateMap.putAll(targetSlotStateMap);
        }
        if (targetUnkonwSlotStateMap != null && targetUnkonwSlotStateMap.size() > 0) {
            dialogState.addToModelStateMap(Constant.UNKNOWN_SLOT_STATE_MAP, targetUnkonwSlotStateMap);
            allSlotStateMap.putAll(targetUnkonwSlotStateMap);
        }

        for (String slotName : allSlotStateMap.keySet()) {
            SlotState slotState = allSlotStateMap.get(slotName);
            List<SlotInfo> slotInfos = slotState.getSlotInfos();
            if(slotInfos != null && slotInfos.size() > 0){
                SlotInfo slotInfo = slotInfos.get(0);
                String value = slotInfo.getValue();
                paramValueMap.put("@" + slotName + "@", value);
            }
        }

        //保存得到的行为预埋的槽位数据（这部分属于用户预埋信息，为定制化的信息）
        if(targetParamValueMap != null && targetParamValueMap.size() > 0){
            for (String slotName : targetParamValueMap.keySet()) {
                if(!PC.PC_PARAM_SET.contains(slotName)){
                    String slotValue = targetParamValueMap.get(slotName);
                    paramValueMap.put("#" + slotName + "#", slotValue);
                }
            }
        }

        //将DMRequest里面的数据作为定制参数加入到paramValueMap中
        paramValueMap.put("#" + CC.SESSION_ID + "#", dialogState.getSessionId());
        paramValueMap.put("#" + CC.TURN_NUM + "#", dialogState.getTotalTurnNum() + "");         //这个数据前面已经设置到了dialogState里面了
        paramValueMap.put("#" + CC.QUERY_TYPE + "#", dmRequest.getRequestType());
        ClientInfo clientInfo = dmRequest.getClientInfo();
        if(clientInfo != null){
            paramValueMap.put("#" + CC.CLIENT_ID + "#", clientInfo.getClientId());
            paramValueMap.put("#" + CC.CHANNEL + "#", clientInfo.getChannel());
            paramValueMap.put("#" + CC.CLIENT_NAME + "#", clientInfo.getClientName());
            paramValueMap.put("#" + CC.CLIENT_TYPE + "#", clientInfo.getClientType());
        }
        UserInfo userInfo = dmRequest.getUserInfo();
        if(userInfo != null){
            paramValueMap.put("#" + CC.USER_ID + "#", userInfo.getUserId());
            paramValueMap.put("#" + CC.USER_NAME + "#", userInfo.getUserName());
        }
        LocationInfo locationInfo = dmRequest.getLocationInfo();
        if(locationInfo != null){
            paramValueMap.put("#" + CC.CITY_NAME + "#", locationInfo.getCityName());
        }
        Map<String, String> extendInfo = dmRequest.getExtendInfo();
        if(extendInfo != null && extendInfo.size() > 0){
            for (String key : extendInfo.keySet()) {
                if(!PC.PC_PARAM_SET.contains(key)){
                    paramValueMap.put("#" + key + "#", extendInfo.get(key));
                }
            }
        }

        //将前面轮次得到的所有业务参数，全部加入到paramValueMap中
        if(bizDataMSMap != null && bizDataMSMap.size() > 0){
            for (String bizItemKey : bizDataMSMap.keySet()) {
                BizDataModelState<String> bizItemValueMS = bizDataMSMap.get(bizItemKey);
                String bizItemValue = bizItemValueMS.getT();
                paramValueMap.put("%" + bizItemKey + "%", bizItemValue);
            }
        }

        dialogState.addToModelStateMap(Constant.PARAM_VALUE_MAP, paramValueMap);
    }
}
