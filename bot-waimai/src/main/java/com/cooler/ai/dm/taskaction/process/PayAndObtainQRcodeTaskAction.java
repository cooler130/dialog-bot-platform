package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.facade.constance.CC;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.model.OrderDataInfo;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.BizDataFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("payAndObtainQRcodeTaskAction")
public class PayAndObtainQRcodeTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(PayAndObtainQRcodeTaskAction.class);

    @Resource
    private BizDataFacade bizDataFacade;

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("SearchFaqTaskAction  （处理动作）查找faq问题的答案");

        //1.（准备Intent值）     获取当前识别到的Intent
        String userId = getPCParamValue(CC.USER_ID);

        //2.设计好传出的数据
        List<OrderDataInfo> orderDataInfos = null;
        String qrCode = null;

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        String unSavedOrderDataJS = getBizDataValue(BC.SELECTED_ORDER_DATA_INFOS);
        List<OrderDataInfo> unSavedOrderDataInfos = JSON.parseArray(unSavedOrderDataJS, OrderDataInfo.class);
        orderDataInfos = bizDataFacade.saveOrderDatas(unSavedOrderDataInfos);                                           //保存了previewOrderDataInfo后，传回来的orderDataInfo就带上了orderId和orderItemIds
        if (userId != null)     bizDataFacade.clearAll(userId);                                                         //订单已经保存到数据库，则清空购物车，后面即使用户不支付，也能从订单服务中取出未支付订单

        qrCode = getQRCode(orderDataInfos);

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addToBizDataMSMap(bizDataMapTmp, BC.SELECTED_ORDER_DATA_INFOS, JSON.toJSONString(orderDataInfos), Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.QR_CODE, JSON.toJSONString(qrCode), Constant.ZERO);

        return bizDataMapTmp;
    }

    //根据订单数据获取二维码
    private String getQRCode(List<OrderDataInfo> orderDataInfos) {
        //todo：这里根据orderDataInfo来计算产生一串二维码数据
        String qrCode = "QRcode->ahgoewjarbenviolrvjnekdvbeoifnrkefeff";
        return qrCode;
    }

    @Override
    public String routeNextProcessCode() {
        return "showQRCode";
    }

}
