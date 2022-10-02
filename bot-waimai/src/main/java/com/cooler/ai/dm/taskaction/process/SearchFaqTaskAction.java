package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("searchFaqTaskAction")
public class SearchFaqTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(SearchFaqTaskAction.class);

//    @Resource
//    private FAQFacade faqFacade;

    @Value("${useRemoteFAQService}")
    private String useRemoteFAQService = null;

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("SearchFaqTaskAction  （处理动作）查找faq问题的答案");
        //0.（准备原句）
        String sentence = getPCParamValue(PC.SENTENCE);

        //1.（准备Intent值）     获取当前识别到的Intent
        String domainName = getPCParamValue(PC.DOMAIN_NAME);
        String transformIntentName = getPCParamValue(PC.TRANSFORM_INTENT_NAME);
        String intentName = getPCParamValue(PC.INTENT_NAME);

        //2.设计好传出的数据
        Map<String, String> searchParams = null;
        String faqAnswer = null;

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        searchParams = new HashMap<>();
        addToMap(searchParams, PC.SENTENCE, sentence);
        addToMap(searchParams, PC.DOMAIN_NAME, domainName);
        addToMap(searchParams, PC.INTENT_NAME, intentName);

        NlgTemplateInfo nlgTemplateInfo = getNlgTemplateInfoOfDefaultTheme(this.getClass(), transformIntentName, intentName, null);//faq-help:下一步，您可以... ;（可以进一步扩展：faq-xxx:...）
        faqAnswer = nlgTemplateInfo.getNlgTemplate();
        if(faqAnswer == null && useRemoteFAQService.equals("true")){
//            faqAnswer = faqFacade.search(searchParams);
        }
        if(faqAnswer == null){
            String defaultIntent = "default";
            NlgTemplateInfo nlgTemplateInfo1 = getNlgTemplateInfoOfDefaultTheme(this.getClass(), defaultIntent, defaultIntent, null);//抱歉！暂时无法解答您的问题！(FAQ)
            faqAnswer = nlgTemplateInfo1.getNlgTemplate();
        }

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addToBizDataMSMap(bizDataMapTmp, BC.FAQ_SEARCH_PARAMS, JSON.toJSONString(searchParams), Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.FAQ_ANSWER, faqAnswer, Constant.ZERO);

        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "showFaqAnswer";
    }



}
