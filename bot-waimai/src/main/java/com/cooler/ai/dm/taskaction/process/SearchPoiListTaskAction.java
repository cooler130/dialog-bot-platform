package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.dm.constant.SC;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.BizDataFacade;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.model.*;
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
@Component("searchPoiListTaskAction")
public class SearchPoiListTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(SearchPoiListTaskAction.class);

    @Resource
    private BizDataFacade bizDataFacade;

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("SearchPoiListTaskAction  （处理动作）按品牌或者商家名条件查询相关商家");

        //1.（准备Intent值）     获取当前识别到的Intent
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);

        //2.设计好传出的数据
        Map<String, String> searchParams = null;
        List<PoiInfo> poiInfos = null;

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        String restaurantName = getBizDataValue(SC.RESTAURANT_NAME);                 //可能是新获取的业务数据，也可能"槽业重名"，故"先槽后业"
        String brand = getBizDataValue(BC.BRAND);
        String orderBy = getBizDataValue(BC.ORDER_BY);
        String sort = getBizDataValue(BC.SORT);
        String currentPoiPageNumStr = getBizDataValueOrDefault(BC.CURRENT_POI_PAGE_NUM, "0");

        searchParams = new HashMap<>();
        switch (intentName){
            case "want_waimai" : {
                addToMap(searchParams, WaimaiConstant.CURRENT_POI_PAGE_NUM, "0");
                break;
            }
            case "reselect_restaurant" : case "continue_ordering" : {
                addToMap(searchParams, WaimaiConstant.CURRENT_POI_PAGE_NUM, "0");
                addToMap(searchParams, WaimaiConstant.POI_NAME, restaurantName);
                break;
            }
            case "turn_page" : case "control_want_more" : {
                addToMap(searchParams, WaimaiConstant.CURRENT_POI_PAGE_NUM, Integer.parseInt(currentPoiPageNumStr) + 1 + "");
                addToMap(searchParams, WaimaiConstant.POI_NAME, restaurantName);
                break;
            }
        }

        addToMap(searchParams, WaimaiConstant.BRAND, brand);
        addToMap(searchParams, WaimaiConstant.ORDER_BY, orderBy);
        addToMap(searchParams, WaimaiConstant.SORT, sort);

        logger.info("SearchPoiListTaskAction 查询条件：{}", JSON.toJSON(searchParams));
        poiInfos = bizDataFacade.getPois(searchParams);

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addToBizDataMSMap(bizDataMapTmp, BC.SEARCH_POI_PARAMS, JSON.toJSONString(searchParams), Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.POI_INFOS, JSON.toJSONString(poiInfos), Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.CURRENT_POI_PAGE_NUM, currentPoiPageNumStr, Constant.FOREVER);

        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "showPoiList";
    }



}
