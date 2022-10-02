package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.facade.constance.CC;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.model.OrderDataInfo;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.BizDataFacade;
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
@Component("createOrderTaskAction")
public class CreateOrderTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(CreateOrderTaskAction.class);

    @Resource
    private BizDataFacade bizDataFacade;

    private static final int GLOBAL_START_ID = 1;
    private static final int POI_SKULIST_STATE = 5;
    private static final int SHOPPINGCART_STATE = 6;
    private static final int PRE_ORDER_STATE = 7;


    private Set<Integer> fromStateIds = new HashSet<>(Arrays.asList(GLOBAL_START_ID, POI_SKULIST_STATE, SHOPPINGCART_STATE));
    private Set<String> intentSets = new HashSet<>(Arrays.asList("order_deal", "pay"));

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("CreateOrderTaskAction  （处理动作）下单请求");

        //1.（准备Intent值）     获取当前识别到的Intent
        String fromStateIdStr = getPCParamValue(PC.FROM_STATE_ID);
        int fromStateId = Integer.parseInt(fromStateIdStr);
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);

        //2.设计好传出的数据
        String isBeyondDeliveryPrice = null;
        String shoppingCartInfoJS = null;
        String selectedOrderDataInfosJS = null;

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        String userId = getPCParamValue(CC.USER_ID);
        String userName = getPCParamValue(CC.USER_NAME);
        AddressInfo defaultAddress = getDefaultAddress(userId);
        isBeyondDeliveryPrice = getBizDataValueOrDefault(BC.IS_BEYOND_DELIVERY_PRICE, "false");
        ShoppingCartInfo  shoppingCartInfo = null;
        List<OrderDataInfo> selectedOrderDataInfos = null;

        switch (intentName) {
            case "order_deal" : case "pay" : {
                if(fromStateIds.contains(fromStateId)){
                    shoppingCartInfo = bizDataFacade.checkShoppingCart(userId);
                    OrderDataInfo orderDataInfo = parseShoppingCartInfo(shoppingCartInfo, userId, userName, defaultAddress);
                    selectedOrderDataInfos = Arrays.asList(orderDataInfo);
                }else if(fromStateId == PRE_ORDER_STATE){
                    String orderDataInfosJS = getBizDataValue(BC.PREVIEW_ORDER_DATA_INFOS);
                    List<OrderDataInfo> preViewOrderDataInfos = JSON.parseArray(orderDataInfosJS, OrderDataInfo.class);
                    OrderDataInfo selectedOrderDataInfo = null;
                    if(preViewOrderDataInfos != null){
                        selectedOrderDataInfo = preViewOrderDataInfos.get(0);
                    }
                    if(selectedOrderDataInfo != null){
                        selectedOrderDataInfos = Arrays.asList(selectedOrderDataInfo);
                    }
                }
                break;
            }
            case "select" : {
                String optionNumberStr = getBizDataValueOrDefault(BC.OPTION_NUMBER, "1");
                int optionNumber = Integer.parseInt(optionNumberStr);
                String orderDataInfosJS = getBizDataValue(BC.PREVIEW_ORDER_DATA_INFOS);
                List<OrderDataInfo> preViewOrderDataInfos = JSON.parseArray(orderDataInfosJS, OrderDataInfo.class);
                OrderDataInfo selectedOrderDataInfo = null;
                if(preViewOrderDataInfos != null){
                    int size = preViewOrderDataInfos.size();
                    if(optionNumber == 0){
                        selectedOrderDataInfo = preViewOrderDataInfos.get(0);
                    }else if(optionNumber > 0 && optionNumber < size){
                        selectedOrderDataInfo = preViewOrderDataInfos.get(optionNumber - 1);
                    }else{
                        selectedOrderDataInfo = null;
                    }
                }
                if(selectedOrderDataInfo != null){
                    selectedOrderDataInfos = Arrays.asList(selectedOrderDataInfo);
                }
                break;
            }
        }

        shoppingCartInfoJS = shoppingCartInfo != null ? JSON.toJSONString(shoppingCartInfo) : Constant.NONE_VALUE;
        selectedOrderDataInfosJS = selectedOrderDataInfos != null ? JSON.toJSONString(selectedOrderDataInfos) : Constant.NONE_VALUE;

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addToBizDataMSMap(bizDataMapTmp, BC.IS_BEYOND_DELIVERY_PRICE, isBeyondDeliveryPrice, Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.SHOPPING_CART_INFO, shoppingCartInfoJS, Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.SELECTED_ORDER_DATA_INFOS, selectedOrderDataInfosJS, Constant.FOREVER);

        return bizDataMapTmp;
    }

    public String routeNextProcessCode() {

        String isBeyondDeliveryPrice = getBizDataValueOrDefault(BC.IS_BEYOND_DELIVERY_PRICE, "false");

        String fromStateIdStr = getPCParamValue(PC.FROM_STATE_ID);
        int fromStateId = Integer.parseInt(fromStateIdStr);

        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);
        String nextProcessCode = null;
        switch (fromStateId){
            case POI_SKULIST_STATE: {
                nextProcessCode = isBeyondDeliveryPrice.equals("true") ? "showOrder" : "showSkuList";
                break;
            }
            case SHOPPINGCART_STATE : case GLOBAL_START_ID :{
                nextProcessCode = isBeyondDeliveryPrice.equals("true") ? "showOrder" : "showShoppingcart";
                break;
            }
            case PRE_ORDER_STATE : {
                if(intentSets.contains(intentName)){                                        //用户没选订单，直接说支付（这种情况很可能发生），将本页第一个作为选择订单让其审核
                    nextProcessCode = isBeyondDeliveryPrice.equals("true") ? "showOrder" : "showShoppingcart";
                }else if(intentName.equals("select")){                                                                  //用户选择了一个订单
                    nextProcessCode = "showOrder";
                }
                break;
            }
        }

        return nextProcessCode;
    }

//    private List<OrderDataInfo> createOrderDataInfos(List<OrderGroupInfo> orderGroupInfos, List<OrderItemInfo> orderItemInfos) {
//        List<OrderDataInfo> orderDataInfos = new ArrayList<>();
//
//        Map<Integer, List<OrderItemInfo>> orderItemInfoMap = new HashMap<>();
//        for (OrderItemInfo orderItemInfo : orderItemInfos) {
//            Integer orderId = orderItemInfo.getOrderId();
//            List<OrderItemInfo> orderItemInfosTmp = orderItemInfoMap.get(orderId);
//            if(orderItemInfosTmp == null){
//                orderItemInfosTmp = new ArrayList<>();
//            }
//            orderItemInfosTmp.add(orderItemInfo);
//            orderItemInfoMap.put(orderId, orderItemInfosTmp);
//        }
//
//
//        for (OrderGroupInfo orderGroupInfo : orderGroupInfos) {
//            Integer orderId = orderGroupInfo.getId();
//            List<OrderItemInfo> orderItemInfosTmp = orderItemInfoMap.get(orderId);
//            OrderDataInfo orderDataInfo = new OrderDataInfo(orderGroupInfo, orderItemInfosTmp, false);
//            orderDataInfos.add(orderDataInfo);
//        }
//
//        return orderDataInfos;
//    }

    private AddressInfo getDefaultAddress(String userId){
        AddressInfo defaultAddress = null;
        List<AddressInfo> addressInfos = bizDataFacade.getAddressesByUserId(userId);
        if(addressInfos != null){
            for (AddressInfo addressInfo : addressInfos) {
                Byte isDefault = addressInfo.getIsDefault();
                if(isDefault == (byte)1){
                    defaultAddress = addressInfo;
                }
            }
            if(defaultAddress == null){
                defaultAddress = addressInfos.get(0);
            }
        }
        return defaultAddress;
    }

    private OrderDataInfo parseShoppingCartInfo(ShoppingCartInfo shoppingCartInfo, String userId, String userName, AddressInfo addressInfo){
        if(shoppingCartInfo == null) return null;
        Map<Integer, Map<Integer, SkuInfo>> selectedSkuInfos = shoppingCartInfo.getSelectedSkuInfos();
        Set<Integer> skuIds = new HashSet<>();
        for (Integer poiId : selectedSkuInfos.keySet()) {
            Map<Integer, SkuInfo> skuInfoMap = selectedSkuInfos.get(poiId);
            skuIds.addAll(skuInfoMap.keySet());
        }
        SortedMap<Integer, Integer> skuCountMap = shoppingCartInfo.getSkuCountMap();
        Map<Integer, PoiInfo> relatedPoiMap = shoppingCartInfo.getRelatedPoiMap();
        Map<Integer, Long> timestampMap = shoppingCartInfo.getTimestampMap();

        List<SkuInfo> skuInfos = bizDataFacade.getSkusByIds(skuIds);

        int i = 1;
        float orderTotalPrice = 0f;
        int orderTotalSkuCount = 0;
        String orderGroupMsg = "";
        List<OrderItemInfo> orderItemInfos = new ArrayList<>();                                                         //构建分订单
        for (SkuInfo skuInfo : skuInfos) {
            Integer skuId = skuInfo.getId();
            Float price = skuInfo.getPrice();
            String skuName = skuInfo.getSkuName();
            String skuMsg = skuInfo.getMsg();
            Integer poiId = skuInfo.getPoiId();
            PoiInfo poiInfo = relatedPoiMap.get(poiId);
            String poiName = poiInfo.getPoiName();
            Integer skuCount = skuCountMap.get(skuId);
            Long timestamp = timestampMap.get(skuId);

            OrderItemInfo orderItemInfo = new OrderItemInfo();
            orderItemInfo.setOrderItemCode("OrderItem_" + userId + "_" + timestamp + "_" + i);
            orderItemInfo.setOrderId(null);
            orderItemInfo.setUserId(userId);
            orderItemInfo.setUserName(userName);
            orderItemInfo.setSkuId(skuId);
            orderItemInfo.setSkuName(skuName);
            orderItemInfo.setPoiId(poiId);
            orderItemInfo.setPoiName(poiName);
            orderItemInfo.setCount(skuCount);
            orderTotalSkuCount += skuCount;
            orderItemInfo.setPrice(price);
            Float skuPrice = skuCount * price;
            orderItemInfo.setTotalPrice(skuPrice);
            orderTotalPrice += skuPrice;
            String orderItemInfoMsg = i + ".) " + poiId + "-" + skuName + ": " + price + " * " + skuCount + " = (" + skuPrice + "); ";
            orderItemInfo.setMsg(orderItemInfoMsg);
            orderItemInfo.setEnable((byte)1);
            orderGroupMsg += orderItemInfoMsg;
            orderItemInfos.add(orderItemInfo);
            i ++;
        }

        OrderGroupInfo orderGroupInfo = new OrderGroupInfo();                                                                          //构建主订单
        orderGroupInfo.setOrderCode("Order_" + userId + "_" + System.currentTimeMillis());
        orderGroupInfo.setUserId(userId);
        orderGroupInfo.setUserName(userName);
        orderGroupInfo.setAddressId(addressInfo != null ? addressInfo.getId() : null);
        orderGroupInfo.setAddress(addressInfo != null ? addressInfo.getAddress() : null);
        orderGroupInfo.setTotalPrice(orderTotalPrice);
        orderGroupInfo.setSkuCount(orderTotalSkuCount);
        orderGroupInfo.setMsg(orderGroupMsg);
        orderGroupInfo.setCreateTimestamp(System.currentTimeMillis());
        orderGroupInfo.setPayTimestamp(-1L);
        orderGroupInfo.setState(0);

        OrderDataInfo orderDataInfo = new OrderDataInfo(orderGroupInfo, orderItemInfos, false);
        return orderDataInfo;
    }

}
