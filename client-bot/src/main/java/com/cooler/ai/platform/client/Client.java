package com.cooler.ai.platform.client;

import com.cooler.ai.platform.client.model.UtilBean;
import com.cooler.ai.platform.client.testcase.BuRouter;
import com.cooler.ai.platform.client.testcase.Callout;
import com.cooler.ai.platform.client.testcase.Waimai;
import com.cooler.ai.platform.facade.DistributionCenterFacade;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DMResponse;
import com.cooler.ai.platform.facade.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static DistributionCenterFacade distributionCenterFacade;

    private static String selectBot = "burouter";

    public static void main(String args[]){
        String initQuery = null;
        Map<String, String[]> querysGroup = null;
        switch (selectBot){
            case "waimai" : {
                initQuery = "signal->waimai|no_intent|shopping|#signal#:init";
                querysGroup = Waimai.querysGroup;
                break;
            }
            case "callout" : {
                initQuery = "signal->callout|no_intent|surveys|#signal#:init";
                querysGroup = Callout.querysGroup;
                break;
            }
            case "burouter" : {
                initQuery = "signal->burouter|no_intent|bu_route|#signal#:init";
                querysGroup = BuRouter.querysGroup;
                break;
            }
        }

//        humanTest(args, initQuery);
        scriptTest(args, initQuery, querysGroup);
    }

    public static void humanTest(String args[], String initQuery){
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("client.xml");
        distributionCenterFacade = (DistributionCenterFacade) beanFactory.getBean("distributionCenterFacade");

        //准备一个带有init信号的DmRequest。
        String signal = "init";
        String sessionId = "SID_TEST_" + System.currentTimeMillis();
        System.out.println("0 ---->驱动信号：" + signal);
        DMRequest dmRequest = UtilBean.createDmRequest(sessionId, Constant.QUERYTYPE_SIGNAL, initQuery);    //信号init驱动，产生欢迎语
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
            dmRequest = UtilBean.createDmRequest(sessionId, queryType, query);


            dmResponse = distributionCenterFacade.distributeProcess(dmRequest);
            System.out.println();
        }
        System.out.println("bye!");

    }

    public static void scriptTest(String[] args, String initQuery, Map<String, String[]> querysGroup) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("client.xml");
        distributionCenterFacade = (DistributionCenterFacade) beanFactory.getBean("distributionCenterFacade");

        String sessionId = null;
        Set<String> groupKeys = querysGroup.keySet();
        int j = 1;
        for (String groupKey : groupKeys) {
            String[] answers = querysGroup.get(groupKey);
            System.out.println("\n开始第 " + (j ++) + " 组 -------------------" + groupKey);
            sessionId = "SID_TEST_" + System.currentTimeMillis();                                                                //变动的sessionID，使组之间不继承上下文。

            //准备一个带有init信号的DmRequest。
            DMRequest dmRequest = UtilBean.createDmRequest(sessionId, Constant.QUERYTYPE_SIGNAL, initQuery);    //信号驱动，产生欢迎语
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
                            System.out.println(turnNum + " ---->机 ：(话术表达）" + messageData + ", " + lastFromStateId + ", (" + fromStateId + ", " + fromStateId2 + ") -> " + message.getIntentCondition() + "-> " + toStateId);
                        }else if(messageType.equals("bubble")){
                            System.out.println(turnNum + " ---->机 ：(操作信息）" + messageData + ", " + lastFromStateId + ", (" + fromStateId + ", " + fromStateId2 + ") -> " + message.getIntentCondition() + "-> " + toStateId);
                        }else if(messageType.equals("data")){
                            System.out.println(turnNum + " 业务数据 ： " + messageData);
                        }
                    }
                }

                String answer = answers[i];
                System.out.println((turnNum) + " ---->人  ：" + answer);

                String queryType = UtilBean.checkQueryType(answer);
                dmRequest = UtilBean.createDmRequest(sessionId, queryType, answer);

                System.out.println();
            }
        }

//        System.exit(0);
    }

}
