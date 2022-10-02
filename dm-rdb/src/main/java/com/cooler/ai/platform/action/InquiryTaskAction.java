package com.cooler.ai.platform.action;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.nlg.NlgConstant;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.nlg.facade.NlgFacade;
import com.cooler.ai.platform.entity.ConditionLogic;
import com.cooler.ai.platform.entity.ConditionRule;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.Message;
import com.cooler.ai.platform.model.BaseConditionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("inquiryTaskAction")
public class InquiryTaskAction extends BaseInteractiveTaskAction {

    private Logger logger = LoggerFactory.getLogger(InquiryTaskAction.class);

    @Autowired
    private NlgFacade nlgFacade;

    @Value("${botName}")
    private String botName;
                                                                                    //接外部话术平台的服务
    @Override
    protected Message createReplyMessage() {
        String reply = "抱歉，暂时无法处理您的请求！！！";

        String firstBadConditionDataJS = dialogState.getParamValue(PC.FIRST_BAD_CONDITION_DATA, Constant.PLATFORM_PARAM);
        BaseConditionData firstBadConditionData = JSON.parseObject(firstBadConditionDataJS, BaseConditionData.class);
        String msgType = Constant.MSG_TEXT;
        if(firstBadConditionData != null){
            ConditionRule conditionRule = firstBadConditionData.getConditionRule();
            Integer conditionRuleId = conditionRule.getId();
            ConditionLogic conditionLogic = firstBadConditionData.getConditionLogic();
            Integer conditionLogicId = conditionLogic.getId();
            Integer transitionId = conditionLogic.getTransitionId();

            String conditionKey = transitionId + "_" + conditionLogicId + "_" + conditionRuleId;
            Map<String, String> paramKvs = new HashMap<>();
            paramKvs.put("condition_key", conditionKey);
            NlgTemplateInfo nlgTemplateInfo = nlgFacade.getNlgSentence(botName, this.getClass().getSimpleName(), Constant.NONE_VALUE, Constant.NONE_VALUE, paramKvs, NlgConstant.V_DEFAULT_THEME);
            String nlgTemplate = nlgTemplateInfo.getNlgTemplate();
            if(nlgTemplate == null ){
                logger.error("管理员请注意：conditionKey 为：" + conditionKey + "的情况，还没有建立对应的话术。");
                nlgTemplateInfo = nlgFacade.getNlgSentence("ALL_BOTs", this.getClass().getSimpleName(), Constant.NONE_VALUE, Constant.NONE_VALUE, null, NlgConstant.V_DEFAULT_THEME);
            }

            String sentence = nlgTemplateInfo.getNlgTemplate();
            logger.info("查找询问话术：" + conditionKey + " --> " + sentence);
            reply = (sentence != null && !sentence.equals("") && !sentence.equals("none")) ? sentence : reply;
        }

        Message message = new Message();
        message.setMessageType(msgType);
        message.setMessageData(reply);
        return message;
    }
}
