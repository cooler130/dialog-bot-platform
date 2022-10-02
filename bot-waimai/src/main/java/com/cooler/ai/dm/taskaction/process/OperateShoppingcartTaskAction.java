package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.BizDataFacade;
import com.cooler.ai.platform.facade.constance.CC;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("operateShoppingcartTaskAction")
public class OperateShoppingcartTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(OperateShoppingcartTaskAction.class);

    @Resource
    private BizDataFacade bizDataFacade;

    @Override
    public Map<String, BizDataModelState<String>> process() {
        //1.（准备Intent值）     获取当前识别到的Intent
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);

        //2.设计好传出的数据
        ShoppingCartInfo shoppingCartInfo = null;

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        String userId = getPCParamValue(CC.USER_ID);
        switch (intentName){
            case "check_shopping_cart" : {
                break;
            }
            case "clear_shopping_cart" : {
                bizDataFacade.clearAll(userId);
                break;
            }
        }
        shoppingCartInfo = bizDataFacade.checkShoppingCart(userId);

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addToBizDataMSMap(bizDataMapTmp, BC.SHOPPING_CART_INFO, JSON.toJSONString(shoppingCartInfo), Constant.ZERO);

        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "showShoppingcart";
    }
}
