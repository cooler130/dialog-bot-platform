package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.dm.external.DataServiceUtil;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.constance.CC;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.model.BuDataInfo;
import com.cooler.ai.platform.model.TQDataInfo;
import com.cooler.ai.platform.model.Order2DataInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("operateTaskAction")
public class OperateTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(OperateTaskAction.class);

    @Resource
    private DataServiceUtil dataServiceUtil;

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("OperateTaskAction  （处理动作）商品排序");

        String policyIdStr = getPCParamValue(PC.POLICY_ID);

        String newTqCountStr = null;
        String newOrderCountStr = null;
        String newBuCountStr = null;

        String newPsCountStr = null;
        String isAboutOrderStr = null;

        List<BuDataInfo> buDataInfos = null;
        switch (policyIdStr){
            case "1" : {
                break;
            }
            case "2" : {
                break;
            }
            case "3" : {
                newPsCountStr = countIncrease(BC.PS_COUNT);
                break;
            }
            case "4" : {
                break;
            }
            case "5" : {
                break;
            }
            case "6" : {
                break;
            }
            case "7" : {
                break;
            }
            case "8" : {
                break;
            }
            case "9" : {
                newTqCountStr =countIncrease(BC.TQ_COUNT);
                break;
            }
            case "10" : {
                newTqCountStr =countIncrease(BC.TQ_COUNT);
                break;
            }
            case "11" : {
                newTqCountStr =countIncrease(BC.TQ_COUNT);
                break;
            }
            case "12" : {
                newOrderCountStr =countIncrease(BC.ORDER_COUNT);
                break;
            }
            case "13" : {
                newOrderCountStr =countIncrease(BC.ORDER_COUNT);
                break;
            }
            case "14" : {
                newOrderCountStr =countIncrease(BC.ORDER_COUNT);
                break;
            }
            case "15" : {
//                newBuCountStr =countIncrease(BC.BU_COUNT);
                newOrderCountStr =countIncrease(BC.ORDER_COUNT);
                break;
            }
            case "16" : {
                newBuCountStr =countIncrease(BC.BU_COUNT);
                buDataInfos = getDefaultBuDataInfos();
                break;
            }
            case "17" : {       //?
//                newBuCountStr =countIncrease(BC.BU_COUNT);
                buDataInfos = getDefaultBuDataInfos();
                break;
            }
            case "18" : {
//                newBuCountStr =countIncrease(BC.BU_COUNT);
                buDataInfos = getDefaultBuDataInfos();
                break;
            }
            case "19" : {
                newPsCountStr = countIncrease(BC.PS_COUNT);
                break;
            }
            case "20" : {
                break;
            }
            case "21" : {
                break;
            }
            case "22" : {       //?
//                newBuCountStr =countIncrease(BC.BU_COUNT);
                break;
            }
            case "24" : {       //?
                newBuCountStr =countIncrease(BC.BU_COUNT);
                break;
            }
        }

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        String fixedTqDataJS = getBizDataValue(BC.FIXED_TQ_DATA);
        if(isEmpty(fixedTqDataJS)){
            String tqDataInfosJS = getBizDataValue(BC.TQ_DATA);
            List<TQDataInfo> tqDataInfos = JSON.parseArray(tqDataInfosJS, TQDataInfo.class);
            if(tqDataInfos != null && tqDataInfos.size() > 0 && tqDataInfos.get(0).getBelief() > 0.9f){
                addToBizDataMSMapIfNotNull(bizDataMapTmp, BC.FIXED_TQ_DATA, JSON.toJSONString(tqDataInfos.get(0)), Constant.FOREVER);
            }
        }

        String fixedOrderDataJS = getBizDataValue(BC.FIXED_ORDER_DATA);
        if(isEmpty(fixedOrderDataJS)){
            String orderDataInfosJS = getBizDataValue(BC.ORDER_DATA);
            List<Order2DataInfo> orderDataInfos = JSON.parseArray(orderDataInfosJS, Order2DataInfo.class);
            if(orderDataInfos != null && orderDataInfos.size() > 0 && orderDataInfos.get(0).getBelief() > 0.9f){
                addToBizDataMSMapIfNotNull(bizDataMapTmp, BC.FIXED_ORDER_DATA, JSON.toJSONString(orderDataInfos.get(0)), Constant.FOREVER);
            }
        }

        String fixedBuDataJS = getBizDataValue(BC.FIXED_BU_DATA);
        if(isEmpty(fixedBuDataJS)){
            String buDataInfosJS = getBizDataValue(BC.BU_DATA);
            List<BuDataInfo> buDataInfosTmp = JSON.parseArray(buDataInfosJS, BuDataInfo.class);
            if(buDataInfosTmp != null && buDataInfosTmp.size() > 0 && buDataInfosTmp.get(0).getBelief() > 0.9f){
                addToBizDataMSMapIfNotNull(bizDataMapTmp, BC.FIXED_BU_DATA, JSON.toJSONString(buDataInfosTmp.get(0)), Constant.FOREVER);
            }
        }

        addToBizDataMSMapIfNotNull(bizDataMapTmp, BC.PS_COUNT, newPsCountStr, Constant.FOREVER);
        addToBizDataMSMapIfNotNull(bizDataMapTmp, BC.TQ_COUNT, newTqCountStr, Constant.FOREVER);
        addToBizDataMSMapIfNotNull(bizDataMapTmp, BC.ORDER_COUNT, newOrderCountStr, Constant.FOREVER);
        addToBizDataMSMapIfNotNull(bizDataMapTmp, BC.BU_COUNT, newBuCountStr, Constant.FOREVER);

        addToBizDataMSMapIfNotNull(bizDataMapTmp, BC.BU_DATA, JSON.toJSONString(buDataInfos), Constant.FOREVER);

        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "answer";
    }


    private String countIncrease(String bcParamName){
        String countStr = getBizDataValueOrDefault(bcParamName, "0");
        Integer count = Integer.parseInt(countStr);
        return (count + 1) + "";
    }

    private List<BuDataInfo> getDefaultBuDataInfos(){
        String userId = getPCParamValue(CC.USER_ID);
        String fixTqDataJS = getPCParamValueOrDefault(BC.FIXED_TQ_DATA, "");
        TQDataInfo fixedTqDataInfo = JSON.parseObject(fixTqDataJS, TQDataInfo.class);
        List<BuDataInfo> buDataInfos = dataServiceUtil.getProbableBuDatas(userId, fixedTqDataInfo);
        return buDataInfos;
    }
}
