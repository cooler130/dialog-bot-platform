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
import java.util.List;
import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("selectSkuTaskAction")
public class SelectSkuTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(SelectSkuTaskAction.class);

    @Resource
    private BizDataFacade bizDataFacade;

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("SelectSkuTaskAction  （处理动作）选择商品");

        //1.（准备Intent值）     获取当前识别到的Intent
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);

        //2.设计好传出的业务数据
        String poiIdStr = null;
        String poiName = null;
        String skuInfosJS = null;
        Integer skuId = null;
        String skuName = null;

        //3.根据不同intentName，获取传出的业务数据
        switch(intentName){
            case "select" : {
                poiIdStr = getBizDataValue(BC.POI_ID);
                poiName = getBizDataValue(BC.POI_NAME);
                if(poiName == null){
                    Map<String, String> searchParams = new HashMap<>();
                    addToMap(searchParams, BC.POI_ID, poiIdStr);
                    List<PoiInfo> poiInfos = bizDataFacade.getPois(searchParams);
                    poiName = poiInfos != null && poiInfos.size() > 0 ? poiInfos.get(0).getPoiName() : null;
                }

                skuInfosJS = getBizDataValue(BC.SKU_INFOS);
                List<SkuInfo> skuInfos = JSON.parseArray(skuInfosJS, SkuInfo.class);

                String optionNumberStr = getBizDataValueOrDefault(BC.OPTION_NUMBER, "1");
                Integer optionNumber = Integer.parseInt(optionNumberStr);
                optionNumber = optionNumber - 1;                                                   //减一是因为数组从0开始

                if(skuInfos.size() >= optionNumber){
                    SkuInfo skuInfo = skuInfos.get(optionNumber);
                    skuId = skuInfo.getId();
                    skuName = skuInfo.getSkuName();
                    String userId = getPCParamValue(CC.USER_ID);
                    bizDataFacade.addSku(userId, skuInfo, 1);                       //选择后，自动加购物车，无需返回购物车数据
                }
                break;
            }
        }

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addToBizDataMSMap(bizDataMapTmp, BC.POI_ID, poiIdStr, Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.POI_NAME, poiName, Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.SKU_INFOS, skuInfosJS, Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.SKU_ID, skuId + "", Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.SKU_NAME, skuName, Constant.FOREVER);
        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "showShoppingcart";
    }
}
