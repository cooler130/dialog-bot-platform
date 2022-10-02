package com.cooler.ai.dm.taskaction.interact;

import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.platform.action.BaseInteractiveTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("showOrderTaskAction")
public class ShowOrderTaskAction extends BaseInteractiveTaskAction {
    private Logger logger = LoggerFactory.getLogger(ShowOrderTaskAction.class);

    private final String SELECT_ORDERS = "select_orders";

    @Override
    protected Message createReplyMessage() {
        Message message = null;
        String msgType = null;
        String msgText = null;
        String selectedOrderDataInfosJS = getBizDataValue(BC.SELECTED_ORDER_DATA_INFOS);

        Map<String, String> paramKvs = new HashMap<>();
        if(isEmpty(selectedOrderDataInfosJS)){
            msgType = Constant.MSG_BUBBLE;
            paramKvs.put(SELECT_ORDERS, "false");   //默认给你下了第一个订单，请确认;
        }else{
            msgType = Constant.MSG_TEXT;
            paramKvs.put(SELECT_ORDERS, "true");    //您的订单如下，请确认！;
        }
        NlgTemplateInfo nlgTemplateInfo = getNlgTemplateInfoOfDefaultTheme(this.getClass(), Constant.NONE_VALUE, Constant.NONE_VALUE, paramKvs);
        msgText = nlgTemplateInfo.getNlgTemplate();

        message = new Message(msgType, msgText);
        return message;
    }
}
