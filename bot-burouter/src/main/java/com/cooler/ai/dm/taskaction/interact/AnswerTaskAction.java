package com.cooler.ai.dm.taskaction.interact;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.action.BaseInteractiveTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.Message;
import com.cooler.ai.platform.model.BuDataInfo;
import com.cooler.ai.platform.model.OrderDataInfo;
import com.cooler.ai.platform.model.TQDataInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("answerTaskAction")
public class AnswerTaskAction extends BaseInteractiveTaskAction {
    private Logger logger = LoggerFactory.getLogger(AnswerTaskAction.class);

    @Override
    protected Message createReplyMessage() {
        String script = null;
        String policyIdStr = getPCParamValue(PC.POLICY_ID);

        String topTqDataInfoName = null;
        String tqDataInfosJS= getBizDataValue(BC.TQ_DATA);
        List<TQDataInfo> tqDataInfos = JSON.parseArray(tqDataInfosJS, TQDataInfo.class);
        if(tqDataInfos != null && tqDataInfos.size() > 0){
            TQDataInfo topTqDataInfo = tqDataInfos.get(0);
            topTqDataInfoName = topTqDataInfo.getTq();
        }

        String topOrderDataInfoName = null;
        String orderDataInfoJS = getBizDataValue(BC.ORDER_DATA);
        List<OrderDataInfo> orderDataInfos = JSON.parseArray(orderDataInfoJS, OrderDataInfo.class);
        if(orderDataInfos != null && orderDataInfos.size() > 0){
            OrderDataInfo topOrderDataInfo = orderDataInfos.get(0);
            topOrderDataInfoName = topOrderDataInfo.getOrder();
        }

        String topBuName = null, secondBuName = null, thirdBuName = null;
        String buDataInfoJS = getBizDataValue(BC.BU_DATA);
        List<BuDataInfo> buDataInfos = JSON.parseArray(buDataInfoJS, BuDataInfo.class);
        if(buDataInfos != null){
            if(buDataInfos.size() > 0){
                BuDataInfo topBu = buDataInfos.get(0);
                topBuName = topBu.getBu();
            }
            if(buDataInfos.size() > 1){
                BuDataInfo secondBu = buDataInfos.get(1);
                secondBuName = secondBu.getBu();
            }
            if(buDataInfos.size() > 2){
                BuDataInfo thirdBu = buDataInfos.get(2);
                thirdBuName = thirdBu.getBu();
            }
        }

        String fixTqName = null;
        String fixTqDataJS = getBizDataValue(BC.FIXED_TQ_DATA);
        TQDataInfo fixTqDataInfo = JSON.parseObject(fixTqDataJS, TQDataInfo.class);
        if(fixTqDataInfo != null){
            fixTqName = fixTqDataInfo.getTq();
        }

        String fixBuName = null;
        String fixBuDataJS = getBizDataValue(BC.FIXED_BU_DATA);
        BuDataInfo fixBuDataInfo = JSON.parseObject(fixBuDataJS, BuDataInfo.class);
        if(fixBuDataInfo != null){
            fixBuName = fixBuDataInfo.getBu();
        }

        switch (policyIdStr){
            case "1" : {
                script = "抱歉！我没有听懂您的问题。";
                break;
            }
            case "2" : {
                script = "###回答faq相关问题";
                break;
            }
            case "3" : {
                script = "我会尽力解答您的问题的。";
                break;
            }
            case "4" : {
                script = "好的，现在为您对接 \"" + fixBuName + " \"业务1。";
                break;
            }
            case "5" : {
                script = "好的，现在为您对接 \"" + fixBuName + " \"业务2。";
                break;
            }
            case "6" : {
                script = "好的，后面回答您的 \"" + fixTqName + "\" 问题。。。。。。";
                break;
            }
            case "7" : {
                script = "好的，后面回答您的 \"" + fixTqName + "\" 问题。。。。。。";
                break;
            }
            case "8" : {
                script = "好的，后面回答您的 \"" + fixTqName + "\" 问题。。。。。。";
                break;
            }
            case "9" : {
                script = "请问您问的是 " + topTqDataInfoName + " 吗？";
                break;
            }
            case "10" : {
                script = "好的，那您问的是 " + topTqDataInfoName + " 吗？";
                break;
            }
            case "11" : {
                script = "好的，那您问的是 " + topTqDataInfoName + " 吗？";
                break;
            }
            case "12" : {
                script = "请问您说的是 " + topOrderDataInfoName + " 这个订单吗1？";
                break;
            }
            case "13" : {
                script = "好的，那您说的是 " + topOrderDataInfoName + " 这个订单吗2？";
                break;
            }
            case "14" : {
                script = "抱歉，您只需要回答是或不是。。。。。。";
                break;
            }
            case "15" : {
                script = "好的，那您说的是 " + topOrderDataInfoName + " 这个订单吗3？";
                break;
            }
            case "16" : {
                script = "您要找 " + topBuName + "、" + secondBuName + " 还是 " + thirdBuName + " 的业务呢1？";
                break;
            }
            case "17" : {
                script = "您要找 " + topBuName + "、" + secondBuName + " 还是 " + thirdBuName + " 的业务呢2？";
                break;
            }
            case "18" : {
                script = "您要找 " + topBuName + "、" + secondBuName + " 还是 " + thirdBuName + " 的业务呢3？";
                break;
            }
            case "19" : {
                script = "好的，现在为您安排人工服务1...";
                break;
            }
            case "20" : {
                script = "好的，现在为您安排人工服务2...";
                break;
            }
            case "21" : {
                script = "好的，现在为您安排人工服务3...";
                break;
            }
            case "22" : {
                script = "好的，现在为您安排人工服务4...";
                break;
            }
            case "23" : {
                script = "您好，我是客服助理，请一句话说出您的需求...";
                break;
            }
            case "24" : {
                script = "好的，那您要找 " + topBuName + "、" + secondBuName + " 还是 " + thirdBuName + " 的业务呢4？";
                break;
            }
        }

        Message message = new Message(Constant.MSG_TEXT, script);
        return message;
    }


}
