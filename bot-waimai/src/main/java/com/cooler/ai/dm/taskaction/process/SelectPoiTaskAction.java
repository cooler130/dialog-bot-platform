package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
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
import java.util.*;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("selectPoiTaskAction")
public class SelectPoiTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(SelectPoiTaskAction.class);

    @Resource
    private BizDataFacade bizDataFacade;

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("SelectPoiTaskAction  （处理动作）选择商家");

        //1.（准备Intent值）     获取当前识别到的Intent
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);

        //2.设计好传出的业务数据
        Integer poiId = null;
        String selectedRestaurantName = null;
        List<SkuInfo> skuInfos = null;

        //3.根据不同intentName，获取传出的业务数据
        switch(intentName){
            case "select" : {
                String poiInfosJS = getBizDataValue(BC.POI_INFOS);
                List<PoiInfo> poiInfos = JSON.parseArray(poiInfosJS, PoiInfo.class);
                if(poiInfos != null){
                    String optionNumberStr = getBizDataValueOrDefault(BC.OPTION_NUMBER, "1");
                    Integer optionNumber = Integer.parseInt(optionNumberStr);
                    optionNumber = optionNumber - 1;                                                   //减一是因为数组从0开始
                    if(poiInfos.size() >= optionNumber){
                        PoiInfo poiInfo = poiInfos.get(optionNumber);
                        selectedRestaurantName = poiInfo.getPoiName();
                        poiId = poiInfo.getId();
                    }
                }

                if(poiId != null){
                    Map<String, String> searchParams = new HashMap<>();
                    addToMap(searchParams, WaimaiConstant.POI_ID, poiId + "");
                    skuInfos = bizDataFacade.getSkus(searchParams);
                }
                break;
            }
            default:{
                logger.error("错误！SelectPoiTaskAction类中，" + intentName + "还没有配置！");
            }
        }

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addToBizDataMSMap(bizDataMapTmp, BC.POI_ID, poiId + "", Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.POI_NAME, selectedRestaurantName, Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.SKU_INFOS, JSON.toJSONString(skuInfos), Constant.FOREVER);
        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "showSkuList";
    }
}
