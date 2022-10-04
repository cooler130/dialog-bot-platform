package com.cooler.ai.dm.taskaction.data;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.action.BaseDataTaskAction;
import com.cooler.ai.platform.facade.BuRouterDataFacade;
import com.cooler.ai.platform.facade.constance.CC;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.model.OrderDataInfo;
import com.cooler.ai.platform.model.TQDataInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2019/1/3
 **/
@Component("orderBeliefDataTaskAction")
public class OrderBeliefDataTaskAction extends BaseDataTaskAction {
    private Logger logger = LoggerFactory.getLogger(OrderBeliefDataTaskAction.class);

    @Resource
    private BuRouterDataFacade buRouterDataFacade;

    @Override
    public void preprocess() {

        String intentName = getPCParamValue(PC.INTENT_NAME);
        switch (intentName) {
            case "positive":{
                String orderDataStr = getBizDataValueOrDefault(BC.ORDER_DATA, "");
                List<OrderDataInfo> orderDataInfos = JSON.parseArray(orderDataStr, OrderDataInfo.class);
                if(orderDataInfos != null && orderDataInfos.size() > 0){
                    orderDataInfos.get(0).setBelief(1.0f);
                    addToPreconditionDataMap(BC.ORDER_DATA, JSON.toJSONString(orderDataInfos));
                    addToPreconditionDataMap(BC.ORDER_BELIEF, "1");
                }
                break;
            }
            case "negative":{
                String orderDataStr = getBizDataValueOrDefault(BC.ORDER_DATA, "");
                List<OrderDataInfo> orderDataInfos = JSON.parseArray(orderDataStr, OrderDataInfo.class);
                if(orderDataInfos != null && orderDataInfos.size() > 0){
                    orderDataInfos.remove(0);
                    addToPreconditionDataMap(BC.ORDER_DATA, JSON.toJSONString(orderDataInfos));
                    addToPreconditionDataMap(BC.ORDER_BELIEF, "0");
                }
                break;
            }
            case "express_question": case "express_order": {
                String userId = getPCParamValue(CC.USER_ID);
                String sentence = getPCParamValue(PC.SENTENCE);
                String fixedTqDataInfoStr = getBizDataValue(BC.FIXED_TQ_DATA);
                TQDataInfo fixedTqDataInfo = JSON.parseObject(fixedTqDataInfoStr, TQDataInfo.class);        //可能fixedTqDataInfoStr为空

                List<OrderDataInfo> orderDataList = buRouterDataFacade.getOrderDatas(userId, sentence, fixedTqDataInfo);
                Collections.sort(orderDataList, new Comparator<OrderDataInfo>() {
                    @Override
                    public int compare(OrderDataInfo o1, OrderDataInfo o2) {
                        if(o1.getBelief() > o2.getBelief()) return -1;
                        else if(o1.getBelief() == o2.getBelief()) return 0;
                        else return 1;
                    }
                });

                List<OrderDataInfo> orderDataInfos = new ArrayList<>();
                if(orderDataList != null){
                    if(orderDataList.size() > 0){
                        orderDataInfos.add(orderDataList.get(0));
                    }
                    if(orderDataList.size() > 1){
                        orderDataInfos.add(orderDataList.get(1));
                    }
                }
                addToPreconditionDataMap(BC.ORDER_DATA, JSON.toJSONString(orderDataInfos));
                if(orderDataInfos.size() > 0) addToPreconditionDataMap(BC.ORDER_BELIEF, orderDataInfos.get(0).getBelief() + "");
                break;
            }
        }
    }

    @Override
    public String getParamValue() {
        String orderBeliefStr = getFromPreconditionDataMap(BC.ORDER_BELIEF);
        return orderBeliefStr == null ? "none" : orderBeliefStr;
    }

}
