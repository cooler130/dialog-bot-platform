package com.cooler.ai.platform.client;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.platform.EntityConstant;
import com.cooler.ai.platform.client.model.UtilBean;
import com.cooler.ai.platform.client.testcase.BuRouter;
import com.cooler.ai.platform.facade.DistributionCenterFacade;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DMResponse;
import com.cooler.ai.platform.facade.model.Message;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

public class Client {
    private static DistributionCenterFacade distributionCenterFacade;

    private static String selectBot = "burouter2";

    public static void main(String args[]){

        String dmType = null;
        String initQuery = null;
        Map<String, String[]> answersGroup = null;
        switch (selectBot){
            case "burouter2" : {
                dmType = Constant.MODEL_GDB;
                initQuery = "signal->burouter2|no_intent|bu_route2|signal:init";
                answersGroup = BuRouter.answersGroup;
                break;
            }
        }

//        humanTest(args, dmType, initQuery);
        scriptTest(dmType, initQuery, answersGroup);
    }

    public static void humanTest(String args[], String dmType, String initQuery){
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("client.xml");
        distributionCenterFacade = (DistributionCenterFacade) beanFactory.getBean("distributionCenterFacade");

        //准备一个带有init信号的DmRequest。
        String signal = "init";
        String sessionId = "SID_TEST_" + System.currentTimeMillis();
        DMRequest dmRequest = UtilBean.createDmRequest(sessionId, dmType, EntityConstant.QUERYTYPE_SIGNAL, initQuery);    //信号init驱动，产生欢迎语
        DMResponse dmResponse = distributionCenterFacade.distributeProcess(dmRequest);
        String query = "";

        int i = 0;
        while(!query.equals("exit")){
            List<Message> messages = dmResponse.getData();
            if(messages != null && messages.size() > 0){
                for (Message message : messages) {
                    String messageType = message.getMessageType();
                    String messageData = message.getMessageData();
                    String lastFromStateId = message.getLastFromStateId();
                    String fromStateId = message.getFromStateId();
                    String toStateId = message.getToStateId();
                    if(messageType.equals("text")){
                        System.out.println(i + " ---->机 ：(回复话术）" + messageData + ", " + lastFromStateId + ", " + fromStateId + " -> " + message.getIntentCondition() + "-> " + toStateId);
                    }else if(messageType.equals("bubble")){
                        System.out.println(i + " ---->机 ：(操作信息）" + messageData + ", " + lastFromStateId + ", " + fromStateId + " -> " + message.getIntentCondition() + "-> " + toStateId);
                    }else if(messageType.equals("data")){
                        System.out.println(i + " 业务数据 ： " + messageData);
                    }
                }
            }
            System.out.println();
            i ++;

            Scanner systemIn = new Scanner(System.in);
            query = systemIn.next();
            System.out.print("\n" + i + "---->人 ：" + query);

            String queryType = UtilBean.checkQueryType(query);
            dmRequest = UtilBean.createDmRequest(sessionId, dmType, queryType, query);


            dmResponse = distributionCenterFacade.distributeProcess(dmRequest);
            System.out.println();
        }
        System.out.println("bye!");

    }

    public static void scriptTest(String dmType, String initQuery, Map<String, String[]> answersGroup) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("client.xml");
        distributionCenterFacade = (DistributionCenterFacade) beanFactory.getBean("distributionCenterFacade");

        String sessionId = null;
        Set<String> groupKeys = answersGroup.keySet();
        int j = 1;
        for (String groupKey : groupKeys) {
            String[] answers = answersGroup.get(groupKey);
            System.out.println("\n开始第 " + (j ++) + " 组 -------------------" + groupKey);
            sessionId = "SID_TEST_" + System.currentTimeMillis();                                                                //变动的sessionID，使组之间不继承上下文。

            //准备一个带有init信号的DmRequest。
            DMRequest dmRequest = UtilBean.createDmRequest(sessionId, dmType, EntityConstant.QUERYTYPE_SIGNAL, initQuery);    //信号驱动，产生欢迎语
            DMResponse dmResponse = null;
            System.out.println("0 ---->驱动信号：init");

            int turnNum = 0;
            for (int i = 0; i < answers.length; i ++) {
                turnNum = i + 1;
                dmResponse = distributionCenterFacade.distributeProcess(dmRequest);
                List<Message> messages = dmResponse.getData();
                if(messages != null && messages.size() > 0){
                    for (Message message : messages) {
                        String messageType = message.getMessageType();
                        String messageData = message.getMessageData();
                        String lastFromStateId = message.getLastFromStateId();
                        String fromStateId = message.getFromStateId();
                        String fromStateId2 = message.getFromStateId2();
                        String toStateId = message.getToStateId();
                        if(messageType.equals("text")){
                            System.out.println(turnNum + " ---->机 ：(话术表达）" + messageData);
                        }else if(messageType.equals("bubble")){
                            System.out.println(turnNum + " ---->机 ：(操作信息）" + messageData);
                        }else if(messageType.equals("data")){
                            System.out.println(turnNum + " 业务数据 ： " + messageData);
                        }
                    }
                }

                String answer = answers[i];
                System.out.println((turnNum) + " ---->人  ：" + answer);

                String queryType = UtilBean.checkQueryType(answer);
                dmRequest = UtilBean.createDmRequest(sessionId, dmType, queryType, answer);

                System.out.println();
            }
        }
    }

}
