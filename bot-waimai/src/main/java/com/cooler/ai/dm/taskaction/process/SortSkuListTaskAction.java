package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.model.EntityConstant;
import com.cooler.ai.platform.model.SkuInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("sortSkuListTaskAction")
public class SortSkuListTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(SortSkuListTaskAction.class);

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("SortSkuListTaskAction  （处理动作）商品排序");

        //1.（准备Intent值）     获取当前识别到的Intent
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);

        //2.设计好传出的数据
        String poiName = null;
        List<SkuInfo> skuInfos = null;

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        String orderBy = getBizDataValue(BC.ORDER_BY);
        String sort = getBizDataValue(BC.SORT);
        sort = (sort == null) ? "asc" : sort;
        final int orderControl = sort.equals("asc") ? 1 : -1;

        switch (intentName) {
            case "sort": {
                String poiInfosJS = getBizDataValue(BC.SKU_INFOS);
                if (poiInfosJS != null) {
                    skuInfos = JSON.parseArray(poiInfosJS, SkuInfo.class);
                    if (orderBy.equals("price")) {
                        Collections.sort(skuInfos, new Comparator<SkuInfo>() {
                            @Override
                            public int compare(SkuInfo o1, SkuInfo o2) {
                                if (o1.getPrice() > o2.getPrice()) return orderControl * 1;
                                if (o1.getPrice() < o2.getPrice()) return orderControl * -1;
                                return 0;
                            }
                        });
                    } else if (orderBy.equals("id")) {
                        Collections.sort(skuInfos, new Comparator<SkuInfo>() {
                            @Override
                            public int compare(SkuInfo o1, SkuInfo o2) {
                                if (o1.getId() > o2.getId()) return orderControl * 1;
                                if (o1.getId() < o2.getId()) return orderControl * -1;
                                return 0;
                            }
                        });
                    }
                }
                poiName = getBizDataValue(BC.POI_NAME);

                break;
            }
        }

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addToBizDataMSMap(bizDataMapTmp, BC.ORDER_BY, orderBy, Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.SORT, sort, Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.POI_NAME, poiName, Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.SKU_INFOS, JSON.toJSONString(skuInfos), Constant.FOREVER);

        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "showSkuList";
    }
}
