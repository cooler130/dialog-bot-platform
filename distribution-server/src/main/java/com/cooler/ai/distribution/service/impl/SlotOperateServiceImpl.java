package com.cooler.ai.distribution.service.impl;

import com.cooler.ai.distribution.EntityConstant;
import com.cooler.ai.distribution.entity.DataVersion;
import com.cooler.ai.distribution.entity.Intent;
import com.cooler.ai.distribution.facade.constance.CC;
import com.cooler.ai.distribution.facade.constance.Constant;
import com.cooler.ai.distribution.facade.constance.PC;
import com.cooler.ai.distribution.facade.model.*;
import com.cooler.ai.distribution.model.DomainDesionData;
import com.cooler.ai.distribution.model.DomainTaskData;
import com.cooler.ai.distribution.model.SlotState;
import com.cooler.ai.distribution.service.*;
import com.cooler.ai.distribution.strategy.LanguageStrategy;
import com.cooler.ai.distribution.strategy.NonLanguageStrategy;
import com.cooler.ai.nlu.SlotInfo;
import lombok.AllArgsConstructor;
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

    @Qualifier("dataVersionService")
    @Autowired
    private DataVersionService dataVersionService;

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

        Map<String, String> globalBizParamMap = null;

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
            //如果这句话完全没有解析出任何意思，那么进入guide-bot（GUIDE_DOMAIN领域），由引导机器人进行话术引导，来帮忙确定出下一轮对话的领域等信息。
            domainDesionData = new DomainDesionData(
                    sessionId, domainTaskData.getTotalTurnNum(), dmRequest.getQuery(),
                    EntityConstant.NO_DOMAIN, EntityConstant.NO_INTENT,
                    EntityConstant.GUIDE_DOMAIN, EntityConstant.NO_INTENT, EntityConstant.NO_INTENT_ID, EntityConstant.NO_TASK,
                    Constant.GLOBAL_START, Constant.GLOBAL_START,
                    false, null, null, null,
                    null, null, null, null
            );
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
        unkonwSlotStateMap = domainDesionData.getUnknownSlotStateMap();
        targetParamValueMap = domainDesionData.getFixedParamValueMap();
        globalBizParamMap = domainDesionData.getGlobalBizParamMap();

        targetIntentId = domainDesionData.getIntentId();
        targetIntent = intentService.selectByIntentId(targetIntentId);

        String requestVersion = dmRequest.getVersion();                                    //请求体指定了版本号，则检查此版本号
        String version = checkVersion(domainName, taskName, requestVersion);

        //5.DialogState装载各类数据（包含槽位数据和系统数据）
        ClientInfo clientInfo = dmRequest.getClientInfo();
        UserInfo userInfo = dmRequest.getUserInfo();

        DialogState dialogState = new DialogState();
        dialogState.setSessionId(sessionId);
        dialogState.setClientId(clientInfo.getClientId());
        dialogState.setChannel(clientInfo.getChannel());
        dialogState.setUserId(userInfo.getUserId());

        dialogState.setInteractiveData(sentence);
        dialogState.setDomainName(domainName);
        dialogState.setTaskName(taskName);
        dialogState.setIntentName(intentName);
        dialogState.setVersion(version);
        //todo：此botName应该通过domainName以及其他参数（clientId、userId、channel等）来获取，前期先使用domainName一个，在DistributionCenterFacadeImpl调用xmlDMFacadeMapService，这个Service中已经建立好了bot和Facade的映射关系，以后可以优化
        dialogState.setBotName(domainName + "Bot");

        domainTaskData.increaseTurnNum(domainName, taskName);                //明确了domainName和taskName后，就要针对各个turnNum加一
        turnNumCacheService.setContext(sessionId + "_" + Constant.DOMAIN_TASK_DATA, domainTaskData);        //上面修改后，要保存的，就不要放到最后保存了
        dialogState.setTotalTurnNum(domainTaskData.getTotalTurnNum());
        dialogState.setDomainTurnNum(domainTaskData.getDomainTurnNum(domainName));
        dialogState.setDomainTaskTurnNum(domainTaskData.getTaskTurnNum(domainName, taskName));
        dialogState.addToModelStateMap(Constant.SO_DOMAIN_DECISION_MAP, domainDecisionDataMap);

        fillParamData(dialogState, dmRequest, sentence, nluDomainName, nluIntentName, targetIntent, targetSlotStateMap, unkonwSlotStateMap, globalBizParamMap, targetParamValueMap, finalSameDomain, targetLastFromState, targetFromState, bizDataMSMap);

        return dialogState;
    }

    /**
     * 根据domainName、taskName，检查以及纠正version值
     * @param domainName
     * @param taskName
     * @param requestVersion
     * @return
     */
    private String checkVersion(String domainName, String taskName, String requestVersion){
        if(requestVersion != null) {
            DataVersion dataVersion = dataVersionService.selectOneVersion(domainName, taskName, requestVersion);
            if(dataVersion != null){                                                                                    //检查version是否存在
                if(dataVersion.getIsOnline() == (byte) 0){                                                                  //如果存在，检查version是否上线
                    DataVersion latestDataVersion = dataVersionService.selectLatestVersion(domainName, taskName);
                    String latestVersion = latestDataVersion.getVersionName();
                    logger.error("请求体中输入参数version值错误，domainName: {} 和 taskName: {} 有此 {} 版本，但没有上线不可用！version参数换成最新版 {} 版！", domainName, taskName, requestVersion, latestVersion);
                    requestVersion = latestVersion;
                }else{
                    logger.info("请求体中输入参数version值验证通过，采用domainName: {} 和 taskName: {} 和 versionName: {} 版本！", domainName, taskName, requestVersion);
                }
            } else {
                DataVersion latestDataVersion = dataVersionService.selectLatestVersion(domainName, taskName);
                String latestVersion = latestDataVersion.getVersionName();
                logger.error("请求体中输入参数version值错误，domainName: {} 和 taskName: {} 没有此 {} 版本发布！version参数换成最新版 {} 版！", domainName, taskName, requestVersion, latestVersion);
                requestVersion = latestVersion;
            }
        }else{                                                                                                          //如果不存在，则设置最新版
            DataVersion latestDataVersion = dataVersionService.selectLatestVersion(domainName, taskName);
            String latestVersion = latestDataVersion.getVersionName();
            requestVersion = latestVersion;
            logger.info("请求体中没有参数version值，采用domainName: {} 和 taskName: {} 和 最新版versionName: {} 版本！", domainName, taskName, requestVersion);
        }
        return requestVersion;
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
                               Map<String, SlotState> targetSlotStateMap, Map<String, SlotState> targetUnkonwSlotStateMap, Map<String, String> globalBizParamMap, Map<String, String> targetParamValueMap,
                               Boolean sameDomain, String lastFromState, String fromState, Map<String, BizDataModelState<String>> bizDataMSMap) {

        //1.保存平台参数（PC）
        Map<String, String> platformParamMap = new HashMap<>();
        platformParamMap.put("$" + PC.SENTENCE + "$", sentence);
        platformParamMap.put("$" + PC.SAME_DOMAIN + "$", sameDomain + "");
        platformParamMap.put("$" + PC.LAST_FROM_STATE + "$", lastFromState + "");
        platformParamMap.put("$" + PC.FROM_STATE + "$", fromState + "");
        platformParamMap.put("$" + PC.NLU_DOMAIN_NAME + "$", targetNluDomainName);
        platformParamMap.put("$" + PC.NLU_INTENT_NAME + "$", targetNluIntentName);
        platformParamMap.put("$" + PC.DOMAIN_NAME + "$", targetIntent.getDomainName());
        platformParamMap.put("$" + PC.INTENT_TYPE + "$", targetIntent.getIntentType() + "");
        platformParamMap.put("$" + PC.VERSION + "$", dialogState.getVersion());
        platformParamMap.put("$" + PC.INTENT_ID + "$", targetIntent.getId() + "");
        platformParamMap.put("$" + PC.INTENT_NAME + "$", targetIntent.getIntentName());
        platformParamMap.put("$" + PC.TASK_NAME + "$", dialogState.getTaskName() + "");
        dialogState.addToParamValueMap(platformParamMap, Constant.PLATFORM_PARAM);

        //2.保存槽位参数，包括意图相关槽位和未识别槽位       （此部分参数存两份，一份分开存到SLOT_STATE_MAP和UNKNOWN_SLOT_STATE_MAP，一份全量存DS）
        Map<String, SlotState> allSlotStateMap = new HashMap<>();
        if(targetSlotStateMap != null && targetSlotStateMap.size() > 0){
            dialogState.addToModelStateMap(Constant.SLOT_STATE_MAP, targetSlotStateMap);
            allSlotStateMap.putAll(targetSlotStateMap);
        }
        if (targetUnkonwSlotStateMap != null && targetUnkonwSlotStateMap.size() > 0) {
            dialogState.addToModelStateMap(Constant.UNKNOWN_SLOT_STATE_MAP, targetUnkonwSlotStateMap);
            allSlotStateMap.putAll(targetUnkonwSlotStateMap);
        }

        Map<String, String> slotParamMap = new HashMap<>();
        for (String slotName : allSlotStateMap.keySet()) {
            SlotState slotState = allSlotStateMap.get(slotName);
            List<SlotInfo> slotInfos = slotState.getSlotInfos();
            if(slotInfos != null && slotInfos.size() > 0){
                SlotInfo slotInfo = slotInfos.get(0);
                String value = slotInfo.getValue();
                slotParamMap.put("@" + slotName + "@", value);
            }
        }
        dialogState.addToParamValueMap(slotParamMap, Constant.SLOT_PARAM);

        Map<String, String> customParamMap = new HashMap<>();
        //3.保存定制化参数，得到的行为预埋的槽位数据（这部分属于用户预埋信息，为定制化的信息）
        if(targetParamValueMap != null && targetParamValueMap.size() > 0){
            for (String slotName : targetParamValueMap.keySet()) {
                if(!PC.PC_PARAM_SET.contains(slotName)){
                    String slotValue = targetParamValueMap.get(slotName);
                    customParamMap.put("#" + slotName + "#", slotValue);
                }
            }
        }

        customParamMap.put("#" + CC.SESSION_ID + "#", dialogState.getSessionId());
        customParamMap.put("#" + CC.TURN_NUM + "#", dialogState.getTotalTurnNum() + "");         //这个数据前面已经设置到了dialogState里面了
        customParamMap.put("#" + CC.QUERY_TYPE + "#", dmRequest.getRequestType());
        ClientInfo clientInfo = dmRequest.getClientInfo();
        if(clientInfo != null){
            customParamMap.put("#" + CC.CLIENT_ID + "#", clientInfo.getClientId());
            customParamMap.put("#" + CC.CHANNEL + "#", clientInfo.getChannel());
            customParamMap.put("#" + CC.CLIENT_NAME + "#", clientInfo.getClientName());
            customParamMap.put("#" + CC.CLIENT_TYPE + "#", clientInfo.getClientType());
        }
        UserInfo userInfo = dmRequest.getUserInfo();
        if(userInfo != null){
            customParamMap.put("#" + CC.USER_ID + "#", userInfo.getUserId());
            customParamMap.put("#" + CC.USER_NAME + "#", userInfo.getUserName());
        }
        LocationInfo locationInfo = dmRequest.getLocationInfo();
        if(locationInfo != null){
            customParamMap.put("#" + CC.CITY_NAME + "#", locationInfo.getCityName());
        }
        Map<String, String> extendInfo = dmRequest.getExtendInfo();
        if(extendInfo != null && extendInfo.size() > 0){
            for (String key : extendInfo.keySet()) {
                if(!PC.PC_PARAM_SET.contains(key)){
                    customParamMap.put("#" + key + "#", extendInfo.get(key));
                }
            }
        }
        dialogState.addToParamValueMap(customParamMap, Constant.CUSTOM_PARAM);

        //保持业务参数集，将前面轮次得到的所有业务参数
        Map<String, String> bizParamMap = new HashMap<>();
        if(globalBizParamMap != null && globalBizParamMap.size() > 0){
            bizParamMap.putAll(globalBizParamMap);
        }

        if(bizDataMSMap != null && bizDataMSMap.size() > 0){
            for (String bizItemKey : bizDataMSMap.keySet()) {
                BizDataModelState<String> bizItemValueMS = bizDataMSMap.get(bizItemKey);
                String bizItemValue = bizItemValueMS.getT();
                bizParamMap.put("%" + bizItemKey + "%", bizItemValue);
            }
        }

        dialogState.addToParamValueMap(bizParamMap, Constant.BIZ_PARAM);
    }
}
