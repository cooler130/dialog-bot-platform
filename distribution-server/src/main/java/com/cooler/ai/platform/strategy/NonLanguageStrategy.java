package com.cooler.ai.platform.strategy;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cooler.ai.platform.EntityConstant;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DialogState;
import com.cooler.ai.platform.model.DomainDesionData;
import com.cooler.ai.platform.entity.Intent;
import com.cooler.ai.platform.model.DomainTaskData;
import com.cooler.ai.platform.service.IntentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component("nonLanguageStrategy")
public class NonLanguageStrategy {
    private Logger logger = LoggerFactory.getLogger(NonLanguageStrategy.class);

    public DomainDesionData decide(DMRequest dmRequest, List<DialogState> historyDialogStates, DomainTaskData domainTaskData, IntentService intentService) {
        String sessionId = dmRequest.getSessionId();
        int totalTurnNum = domainTaskData.getTotalTurnNum();
        String sentence = dmRequest.getRequestType() + "类型数据";

        DialogState lastDialogState = null;
        String targetLastFromState = null;
        String targetFromState = null;
        if (historyDialogStates != null && historyDialogStates.size() > 0) {
            lastDialogState = historyDialogStates.get(0);
            if(lastDialogState != null){
                targetLastFromState = lastDialogState.getParamValue(PC.FROM_STATE2, Constant.PLATFORM_PARAM);
                targetFromState = lastDialogState.getParamValue(PC.TO_STATE, Constant.PLATFORM_PARAM);
            }
        }
        targetLastFromState = targetLastFromState != null ? targetLastFromState : EntityConstant.GLOBAL_START;
        targetFromState = targetFromState != null ? targetFromState : EntityConstant.GLOBAL_START;

        Map<String, String> extendInfo = dmRequest.getExtendInfo();
        if (extendInfo == null || extendInfo.size() == 0) {
            logger.error("3.2.警告！进入动作处理分支，但extendInfo为null，故而进入兜底策略");
            return new DomainDesionData(sessionId,
                    totalTurnNum,
                    sentence,
                    EntityConstant.NO_DOMAIN,
                    EntityConstant.NO_INTENT,
                    EntityConstant.GUIDE_DOMAIN,
                    EntityConstant.NO_INTENT,
                    EntityConstant.NO_INTENT_ID,
                    EntityConstant.NO_TASK,
                    targetLastFromState,
                    targetFromState,
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        } else {
            String domainName = extendInfo.get(PC.DOMAIN_NAME);
            String taskName = extendInfo.get(PC.TASK_NAME);
            String intentName = extendInfo.get(PC.INTENT_NAME);
            String paramValueMapJS = extendInfo.get(Constant.PARAM_VALUE_MAP);

            String nluDomainName = domainName;
            String nluIntentName = intentName;


            if (intentName != null && domainName != null && taskName != null) {
                Intent targetIntent = intentService.selectByTwoNames(domainName, intentName);
                if (targetIntent != null) {
                    Integer targetIntentId = targetIntent.getId();
                    boolean finalSameDomain = true;

                    Map<String, String> fixedParamValueMap = JSONObject.parseObject(paramValueMapJS, new TypeReference<Map<String, String>>() {});

                    return new DomainDesionData(sessionId,
                            totalTurnNum,
                            sentence,
                            nluDomainName,
                            nluIntentName,
                            domainName,
                            intentName,
                            targetIntentId,
                            taskName,
                            targetLastFromState,
                            targetFromState,
                            finalSameDomain,
                            null,
                            null,
                            null,
                            null,
                            null,
                            fixedParamValueMap
                            );
                } else {
                    logger.error("3.4.系统错误！前端数据传来的domainName和intentName居然在后端intent表中没有声明，请检查 domainName：" + domainName + " 和 intentName ： " + intentName + "是否在intent表中。");
                }
            } else {
                logger.error("3.5.系统错误！前端数据体没有预埋的intentName和domainName，请检查前端本轮数据体的格式，以及前面如何构造此数据体！");
            }
            return new DomainDesionData(sessionId,
                    totalTurnNum,
                    sentence,
                    nluDomainName,
                    nluIntentName,
                    domainName,
                    intentName,
                    EntityConstant.NO_INTENT_ID,
                    taskName,
                    targetLastFromState,
                    targetFromState,
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }
}
