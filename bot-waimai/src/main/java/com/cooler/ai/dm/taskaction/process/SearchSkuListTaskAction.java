package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.dm.constant.SC;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.model.*;
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
@Component("searchSkuListTaskAction")
public class SearchSkuListTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(SearchSkuListTaskAction.class);

    @Resource
    private BizDataFacade bizDataFacade;

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("SearchSkuListTaskAction  （处理动作）根据搜索词查询相关的商品");

        //1.（准备Intent值）     获取当前识别到的Intent
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);

        //2.设计好传出的数据
        Map<String, String> searchParams = null;
        String selectedPoiName = null;
        List<SkuInfo> skuInfos = null;

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        String skuName = getBizDataValue(SC.DISH_NAME);
        skuName = skuName != null ? skuName : getBizDataValue(BC.SKU_NAME);
        String moreThanPrice = getBizDataValue(BC.MORE_THAN_PRICE);
        String lessThanPrice = getBizDataValue(BC.LESS_THAN_PRICE);
        String poiId = getBizDataValue(BC.POI_ID);
        String orderBy = getBizDataValue(BC.ORDER_BY);
        String sort = getBizDataValue(BC.SORT);
        String currentSkuPageNum = getBizDataValueOrDefault(BC.CURRENT_SKU_PAGE_NUM, "0");

        searchParams = new HashMap<>();
        switch (intentName){
            case "search" : {
                addToMap(searchParams, WaimaiConstant.CURRENT_SKU_PAGE_NUM, currentSkuPageNum);                         //当前页

                addToMap(searchParams, WaimaiConstant.SKU_NAME, skuName);
                addToMap(searchParams, WaimaiConstant.MORE_THAN_PRICE, moreThanPrice);
                addToMap(searchParams, WaimaiConstant.LESS_THAN_PRICE, lessThanPrice);
                addToMap(searchParams, WaimaiConstant.POI_ID, poiId);
                addToMap(searchParams, WaimaiConstant.ORDER_BY, orderBy);
                addToMap(searchParams, WaimaiConstant.SORT, sort);
                break;
            }
            case "turn_page" : case "control_want_more" : {
                currentSkuPageNum = Integer.parseInt(currentSkuPageNum) + 1 + "";                                       //当前页+1
                addToMap(searchParams, WaimaiConstant.CURRENT_SKU_PAGE_NUM, currentSkuPageNum);

                addToMap(searchParams, WaimaiConstant.SKU_NAME, skuName);
                addToMap(searchParams, WaimaiConstant.MORE_THAN_PRICE, moreThanPrice);
                addToMap(searchParams, WaimaiConstant.LESS_THAN_PRICE, lessThanPrice);
                addToMap(searchParams, WaimaiConstant.POI_ID, poiId);
                addToMap(searchParams, WaimaiConstant.ORDER_BY, orderBy);
                addToMap(searchParams, WaimaiConstant.SORT, sort);
                break;
            }
            case "continue_ordering" : {    //继续购物
                addToMap(searchParams, WaimaiConstant.CURRENT_SKU_PAGE_NUM, currentSkuPageNum);                         //当前页

                addToMap(searchParams, WaimaiConstant.MORE_THAN_PRICE, moreThanPrice);
                addToMap(searchParams, WaimaiConstant.LESS_THAN_PRICE, lessThanPrice);
                addToMap(searchParams, WaimaiConstant.POI_ID, poiId);
                addToMap(searchParams, WaimaiConstant.ORDER_BY, orderBy);
                addToMap(searchParams, WaimaiConstant.SORT, sort);
                break;
            }
            case "order_deal": case "pay" : {    //继续购物
                addToMap(searchParams, WaimaiConstant.CURRENT_SKU_PAGE_NUM, currentSkuPageNum);                         //当前页

                addToMap(searchParams, WaimaiConstant.MORE_THAN_PRICE, moreThanPrice);
                addToMap(searchParams, WaimaiConstant.LESS_THAN_PRICE, lessThanPrice);
                addToMap(searchParams, WaimaiConstant.POI_ID, poiId);
                addToMap(searchParams, WaimaiConstant.ORDER_BY, orderBy);
                addToMap(searchParams, WaimaiConstant.SORT, sort);
                break;
            }
            default:{
                logger.error("错误！SearchSkuListTaskAction，" + intentName + "还没有配置！");
            }
        }

        logger.info("SearchSkuListTaskAction 查询条件：{}", JSON.toJSON(searchParams));
        skuInfos = bizDataFacade.getSkus(searchParams);
        if(!isEmpty(poiId)){
            Integer poiIdInt = Integer.parseInt(poiId);
            PoiInfo poiInfo = bizDataFacade.selectPoiById(poiIdInt);
            selectedPoiName = poiInfo.getPoiName();
        }

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addToBizDataMSMap(bizDataMapTmp, BC.SEARCH_SKU_PARAMS, JSON.toJSONString(searchParams), Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.SKU_INFOS, JSON.toJSONString(skuInfos), Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.CURRENT_SKU_PAGE_NUM, currentSkuPageNum, Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.POI_NAME, selectedPoiName, Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.SKU_NAME, skuName, Constant.FOREVER);
        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "showSkuList";
    }
}
