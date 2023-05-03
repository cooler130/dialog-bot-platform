package com.cooler.ai.distribution.client;

import com.cooler.ai.distribution.client.testcase.BossHiring;
import com.cooler.ai.distribution.facade.constance.Constant;
import com.cooler.ai.distribution.facade.model.DMRequest;
import com.cooler.ai.distribution.EntityConstant;
import com.cooler.ai.distribution.client.model.UtilBean;
import com.cooler.ai.distribution.client.testcase.BuRouter;
import com.cooler.ai.distribution.facade.DistributionCenterFacade;
import com.cooler.ai.distribution.facade.model.DMResponse;
import com.cooler.ai.distribution.facade.model.Message;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

public class Client {
    private static DistributionCenterFacade distributionCenterFacade;

    private static String selectBot = "boss_hiring";

    public static void main(String args[]){

        String initQuery = null;
        Map<String, String[]> answersGroup = null;
        switch (selectBot){
            case "burouter2" : {
                initQuery = "signal->burouter2|no_intent|bu_route2|signal:init";                    //->之前为访问类型，详见EntityConstant的访问类型
                answersGroup = BuRouter.answersGroup;
                break;
            }
            case "boss_hiring" : {
                initQuery = "text->no_text";
                answersGroup = BossHiring.answersGroup;
                break;
            }
        }

//        humanTest(args, dmType, initQuery);
        scriptTest(initQuery, answersGroup);
    }

    //主动交互
    public static void scriptTest(String initQuery, Map<String, String[]> answersGroup) {
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
            DMRequest dmRequest = null;
            DMResponse dmResponse = null;
            if(!initQuery.endsWith("->no_text")){
                dmRequest = UtilBean.createDmRequest(sessionId, initQuery);
                System.out.println("0 ---->驱动信号：init");
            }

            int turnNum = 0;
            for (int i = 0; i < answers.length; i ++) {
                turnNum = i + 1;
                if(dmRequest != null){
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
                }

                String answer = answers[i];
                System.out.println((turnNum) + " ---->人  ：" + answer);

                dmRequest = UtilBean.createDmRequest(sessionId, answer);

                System.out.println();
            }
        }
    }


    public static void humanTest(String args[], String dmType, String initQuery){
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("client.xml");
        distributionCenterFacade = (DistributionCenterFacade) beanFactory.getBean("distributionCenterFacade");

        //准备一个带有init信号的DmRequest。
        String signal = "init";
        String sessionId = "SID_TEST_" + System.currentTimeMillis();
        DMRequest dmRequest = UtilBean.createDmRequest(sessionId, initQuery);    //信号init驱动，产生欢迎语
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

            dmRequest = UtilBean.createDmRequest(sessionId, query);


            dmResponse = distributionCenterFacade.distributeProcess(dmRequest);
            System.out.println();
        }
        System.out.println("bye!");

    }


}
