package com.cooler.ai.platform.client.biz;

import com.cooler.ai.nlg.NlgConstant;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.nlg.facade.NlgFacade;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.*;

/**
 * Created by zhangsheng on 2018/7/17.
 */
public class BizDataClient {

    private static NlgFacade nlgFacade;

    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("client.xml");
        nlgFacade = (NlgFacade) beanFactory.getBean("nlgFacade");

        try {
            testGetNlgSentence();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testGetNlgSentence(){
        String dmName = "test_DM";
        String actionName = "test_action";
        String transformIntentName = "test_transform_intent_name";
        String intentName = "test_intent_name";


        Map<String, String> paramKvs = new HashMap<>();
        paramKvs.put("key", "333");                                     //此为模板匹配条件值，用来匹配waimai-showList-want_waimai具体的模板

        paramKvs.put("placeName", "中关村");                             //placeName和person为模板变量值，用来替代模板中的变量
        paramKvs.put("person", "张三");

        NlgTemplateInfo nlgTemplateInfo = nlgFacade.getNlgSentence(dmName, actionName, transformIntentName, intentName, paramKvs, NlgConstant.V_DEFAULT_THEME);
        String nlgSentence =nlgTemplateInfo.getNlgTemplate();

        System.out.println(nlgSentence);
    }

}
