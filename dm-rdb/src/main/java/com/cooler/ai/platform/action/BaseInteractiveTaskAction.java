package com.cooler.ai.platform.action;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.nlg.NlgConstant;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.*;

import java.util.*;
import java.util.List;

public class BaseInteractiveTaskAction extends BaseTaskAction implements InteractiveTaskAction<DMRequest, DialogState, BizDataModelState<String>> {

    protected String processActionName = null;

    @Override
    public void setProcessActionName(String processActionName) {
        this.processActionName = processActionName;
    }

    @Override
    public DMResponse interActive() {
        List<Message> messages = createMessages();
        String dialogActName = defineDialogAct();
        Map<String, String> extMap = createExtMap();
        DMResponse dmResponse = createDMResponse(1, null, botName, dmRequest.getSessionId(), dialogState.getTotalTurnNum(), messages, dialogActName, extMap);
        return dmResponse;
    }

//----------------------------------------------------此为模板模式，有下面这些扩展点，在run方法中提供了创建默认DMResponse，也支持在子类中扩展出特殊的DMResponse。

    /**
     * 创建回复文本和相关参数集，（此方法可以被子类重写，进而在子类中做一些额外处理， 是一个扩展点）（传入dmRequest、dialogState、globalMap，就是为了让子类重载的过程中自己使用这些量来创建Message，子类不重载，则使用下列方法产生的默认Message）
     * @return  文本和参数集组成的Message
     */
    protected List<Message> createMessages(){
        List<Message> messages = new ArrayList<>();

        //回复消息体是必须的消息体
        Message replyMessage = createReplyMessage();
        //有业务数据则有数据消息体
        Message dataMassage = createDataMessage();

        //其它消息体是扩展的消息体，视业务确定
        Message oneMessage = createOtherMessage();
        List<Message> manyMessages = createOtherMessages();

        String newReply = null;
        if(replyMessage != null)  {
            newReply = replyMessage.getMessageData();                        //这里所得到的回复语可能在各个InteractiveTask中有所改变，需要这里重新设置一下
        }else{
            newReply = "刚才没听懂，请您换种说法，再说一遍吧！";
            replyMessage = new Message();
            replyMessage.setMessageType(Constant.MSG_TEXT);
            replyMessage.setMessageData(newReply);
        }
        bizDataMSMap.put(Constant.REPLY, new BizDataModelState<>(
                dialogState.getSessionId(),
                dialogState.getTotalTurnNum(),
                dialogState.getDomainName(),
                dialogState.getTaskName(),
                dialogState.getDomainTaskTurnNum(),
                dialogState.getBotName(),
                Constant.REPLY,
                newReply,
                Constant.FOREVER
        ));

        messages.add(replyMessage);
        if(dataMassage != null) messages.add(dataMassage);
        if(oneMessage != null) messages.add(oneMessage);
        if(manyMessages != null) messages.addAll(manyMessages);
        messageProcess(messages);

        return messages;
    }

    /**
     * 默认的回复消息创建
     * @return
     */
    protected Message createReplyMessage(){
        NlgTemplateInfo nlgTemplateInfo = nlgFacade.getNlgSentence(botName, processActionName, Constant.NONE_VALUE, Constant.NONE_VALUE, null, themeWord);
        String reply = nlgTemplateInfo.getNlgTemplate();
        if(reply == null && !themeWord.equals(NlgConstant.V_DEFAULT_THEME)){
            nlgTemplateInfo = nlgFacade.getNlgSentence(botName, processActionName, Constant.NONE_VALUE, Constant.NONE_VALUE, null, NlgConstant.V_DEFAULT_THEME);
            reply = nlgTemplateInfo.getNlgTemplate();
        }

        if(reply != null && !reply.equals("none") && !reply.contains("${")){                                            //展示的话术应该不含变量
            Message message = new Message();
            message.setMessageType(Constant.MSG_TEXT);
            message.setMessageData(reply);
            return message;
        }
        return null;
    }

    /**
     * 默认的数据消息创建
     * @return
     */
    protected Message createDataMessage(){
        Message message = new Message();
        message.setMessageType(Constant.MSG_DATA);
        Map<String, String> bizValueMap = new HashMap<>();
        for (String bizItemName : bizDataMSMap.keySet()) {
            BizDataModelState<String> dataModelState = bizDataMSMap.get(bizItemName);
            int turnNum = dataModelState.getTurnNum();
            if(turnNum == dialogState.getTotalTurnNum()){
                String bizItemValue = dataModelState.getT();
                bizValueMap.put(bizItemName, bizItemValue);
            }
        }
        if(bizValueMap.size() > 0){
            message.setMessageData(JSON.toJSONString(bizValueMap));
            return message;
        }
        return null;
    }

    /**
     * 其它用于重写的方法，创建单个消息体
     * @return
     */
    protected Message createOtherMessage(){
        return null;
    }

    /**
     * 其它用于重写的方法，创建多个消息体
     * @return
     */
    protected List<Message> createOtherMessages(){
        return null;
    }

    /**
     * 此方法可重写，对指定的消息体进一步处理，（此处理过程是指定了text、bubble类型中添加状态迁移过程数据）
     * @param messages
     */
    protected void messageProcess(List<Message> messages){
        Set<String> extraLogMessageTypes = new HashSet<>(Arrays.asList(Constant.MSG_TEXT, Constant.MSG_BUBBLE));
        if(messages != null) {                                                                                          //增加text数据的额外日志
            for (Message message : messages) {
                String messageType = message.getMessageType();
                if(extraLogMessageTypes.contains(messageType)){
                    message.setLastFromStateId(dialogState.getParamValueOrDefault(PC.LAST_FROM_STATE_ID, Constant.PLATFORM_PARAM, "-1"));
                    message.setFromStateId(dialogState.getParamValueOrDefault(PC.FROM_STATE_ID, Constant.PLATFORM_PARAM, "-1"));
                    message.setFromStateId2(dialogState.getParamValueOrDefault(PC.FROM_STATE_ID2, Constant.PLATFORM_PARAM, "-1"));
                    String intentName = dialogState.getParamValueOrDefault(PC.INTENT_NAME, Constant.PLATFORM_PARAM, "noIntent");
                    String transformIntentName = dialogState.getParamValueOrDefault(PC.TRANSFORM_INTENT_NAME, Constant.PLATFORM_PARAM, "noIntent");
                    message.setIntentCondition("[" + intentName + " -> " + transformIntentName + "]");
                    message.setToStateId(dialogState.getParamValueOrDefault(PC.TO_STATE_ID, Constant.PLATFORM_PARAM, "-1"));
                }
            }
        }
    }

    /**
     * 定义dialogAct（此方法可以被子类重写，进而在子类中做一些额外处理， 是一个扩展点）
     * @return
     */
    protected String defineDialogAct() {
        return null;
    }

    /**
     * 创建ExtMap（此方法可以被子类重写，进而在子类中做一些额外处理， 是一个扩展点）
     * @return
     */
    protected Map<String,String> createExtMap() {
        return null;
    }

    //----------------------------------------------------本类内部生产DMResponse和对外传送DMResponse的方法
    /** 设置DMResponse结构体 **/
    protected DMResponse createDMResponse(int code, String msg, String dmName, String sessionId, int turnNum, List<Message> data, String dialogActName, Map<String, String> extMap) {
        DMResponse dmResponse = new DMResponse();
        dmResponse.setCode(code);
        dmResponse.setMsg(msg);

        dmResponse.setDmName(dmName);
        dmResponse.setSessionId(sessionId);
        dmResponse.setTurnNum(turnNum);
        dmResponse.setData(data);
        dmResponse.setDialogActName(dialogActName);
        dmResponse.setExtMap(extMap);
        return dmResponse;
    }
}
