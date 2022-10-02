package com.cooler.ai.dm.taskaction.data;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.action.BaseDataTaskAction;
import com.cooler.ai.platform.facade.BizDataFacade;
import com.cooler.ai.platform.facade.constance.CC;
import com.cooler.ai.platform.model.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2019/1/3
 **/
@Component("isBeyondDeliveryPriceDataTaskAction")
public class IsBeyondDeliveryPriceDataTaskAction extends BaseDataTaskAction {

    @Resource
    private BizDataFacade bizDataFacade;

    @Override
    public String getParamValue() {

        String userId = getPCParamValue(CC.USER_ID);

        //1.检测购物车中的商品是否超过起送价
        ShoppingCartInfo shoppingCartInfo = bizDataFacade.checkShoppingCart(userId);
        if(shoppingCartInfo != null) {
            Map<Integer, Map<Integer, SkuInfo>> selectedPoiSkusMap = shoppingCartInfo.getSelectedSkuInfos();
            SortedMap<Integer, Integer> skuCountMap = shoppingCartInfo.getSkuCountMap();

            Float totalPriceInShoppingCart = 0.0f;
            for (Integer poiId : selectedPoiSkusMap.keySet()) {
                Map<Integer, SkuInfo> skusMap = selectedPoiSkusMap.get(poiId);
                for (Integer skuId : skusMap.keySet()) {
                    Integer skuCount = skuCountMap.get(skuId);
                    SkuInfo skuInfo = skusMap.get(skuId);
                    Float price = skuInfo.getPrice();
                    Float skuTotalPrice = price * skuCount;
                    totalPriceInShoppingCart += skuTotalPrice;
                }
            }
            boolean shoppingCartIsBeyondDeliveryPrice = totalPriceInShoppingCart >= BC.DELIVERY_PRICE;
            if(shoppingCartIsBeyondDeliveryPrice)   return "true";
        }

        //2.检测预选的订单数据是否超过起送价（PRE_ORDER_DATA_INFO保存一轮）
        String selectedOrderDataInfosJS = getBizDataValue(BC.SELECTED_ORDER_DATA_INFOS);
        if(selectedOrderDataInfosJS != null){
            List<OrderDataInfo> selectedOrderDataInfos = JSON.parseArray(selectedOrderDataInfosJS, OrderDataInfo.class);
            if(selectedOrderDataInfos != null && selectedOrderDataInfos.size() > 0){
                float totalPriceInOrderGroup = 0f;
                for (OrderDataInfo selectedOrderDataInfo : selectedOrderDataInfos) {
                    OrderGroupInfo orderGroupInfo = selectedOrderDataInfo.getOrderGroupInfo();
                    totalPriceInOrderGroup += orderGroupInfo.getTotalPrice();
                }
                if(totalPriceInOrderGroup > BC.DELIVERY_PRICE)  return "true";
            }
        }

        return "false";
    }

}
