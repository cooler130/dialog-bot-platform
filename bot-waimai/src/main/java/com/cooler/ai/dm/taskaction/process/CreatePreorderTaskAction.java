package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("createPreorderTaskAction")
public class CreatePreorderTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(CreatePreorderTaskAction.class);

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("CreatePreorderTaskAction  （处理动作）创建预览订单");

        //1.（准备Intent值）     获取当前识别到的Intent

        //2.设计好传出的数据
        String currentOrderPageNumStr = Constant.NONE_VALUE;
        String searchedOrderStateStr = Constant.NONE_VALUE;
        String searchedOrderTotalSizeStr = Constant.NONE_VALUE;
        String orderCountPerPageStr = Constant.NONE_VALUE;
        String orderCountThisPageStr = Constant.NONE_VALUE;
        String orderDataInfosJS = Constant.NONE_VALUE;

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        //本action所需要的数据在HasOrderDataTaskAction类中的预处理过程已经分别获得了，所以这里只需要获取并存储即可
        String orderDataInfosMapJS = getBizDataValue(BC.ORDER_DATA_INFOS_MAP);                                                                   //从历史订单中选择的要"再来一单"的订单数据集
        Map<String, String> orderDataInfosMap = JSON.parseObject(orderDataInfosMapJS, Map.class);

        if(orderDataInfosMap != null){
            currentOrderPageNumStr = orderDataInfosMap.get(WaimaiConstant.SEARCHED_PAGE_NUM);
            searchedOrderStateStr = orderDataInfosMap.get(WaimaiConstant.SEARCHED_ORDER_STATE);
            searchedOrderTotalSizeStr = orderDataInfosMap.get(WaimaiConstant.SEARCHED_ORDER_TOTAL_SIZE);
            orderCountPerPageStr = orderDataInfosMap.get(WaimaiConstant.MAX_ORDER_COUNT_PER_PAGE);
            orderCountThisPageStr = orderDataInfosMap.get(WaimaiConstant.ORDER_COUNT_THIS_PAGE);
            orderDataInfosJS = orderDataInfosMap.get(WaimaiConstant.ORDER_DATA_INFOS);
        }

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addToBizDataMSMap(bizDataMapTmp, BC.CURRENT_ORDER_PAGE_NUM, currentOrderPageNumStr, Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.CURRENT_ORDER_STATE, searchedOrderStateStr, Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.ORDER_SIZE, searchedOrderTotalSizeStr, Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.ORDER_COUNT_PER_PAGE, orderCountPerPageStr, Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.ORDER_COUNT_THIS_PAGE, orderCountThisPageStr, Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.PREVIEW_ORDER_DATA_INFOS, orderDataInfosJS, Constant.ONE);                       //预览的订单数据，此数据只能保存一轮，如果下一轮不去支付，则消失
        return bizDataMapTmp;
    }

    public String routeNextProcessCode() {
       return "showPreorder";
    }

}
