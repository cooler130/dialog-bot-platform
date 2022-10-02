package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.model.EntityConstant;
import com.cooler.ai.platform.model.PoiInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("sortPoiListTaskAction")
public class SortPoiListTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(SortPoiListTaskAction.class);

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("SortPoiListTaskAction  （处理动作）商家排序");

        //1.（准备Intent值）     获取当前识别到的Intent
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);

        //2.设计好传出的数据
        List<PoiInfo> poiInfos = null;

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        String orderBy = getBizDataValue(BC.ORDER_BY);
        String sort = getBizDataValue(BC.SORT);
        sort = (sort == null) ? "asc" : sort;
        final int orderControl = sort.equals("asc") ? 1 : -1;

        switch (intentName){
            case "sort" : {
                String poiInfosJS = getBizDataValue(BC.POI_INFOS);
                if(poiInfosJS != null){
                    poiInfos = JSON.parseArray(poiInfosJS, PoiInfo.class);
                    if(orderBy.equals("price")) {
                        Collections.sort(poiInfos, new Comparator<PoiInfo>() {
                            @Override
                            public int compare(PoiInfo o1, PoiInfo o2) {
                                if (o1.getAvgPrice() > o2.getAvgPrice()) return orderControl * 1;
                                if (o1.getAvgPrice() < o2.getAvgPrice()) return orderControl * -1;
                                return 0;
                            }
                        });
                    }else if(orderBy.equals("id")){
                        Collections.sort(poiInfos, new Comparator<PoiInfo>() {
                            @Override
                            public int compare(PoiInfo o1, PoiInfo o2) {
                                if (o1.getId() > o2.getId()) return orderControl * 1;
                                if (o1.getId() < o2.getId()) return orderControl * -1;
                                return 0;
                            }
                        });
                    }
                }
                break;
            }
        }

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addToBizDataMSMap(bizDataMapTmp, BC.ORDER_BY, orderBy, Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.SORT, sort, Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.POI_INFOS, JSON.toJSONString(poiInfos), Constant.FOREVER);

        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "showPoiList";
    }
}
