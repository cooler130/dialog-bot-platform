package com.cooler.ai.dm.taskaction.interact;

import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.nlg.NlgConstant;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.platform.action.BaseInteractiveTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.Message;
import com.cooler.ai.platform.model.EntityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("showShoppingcartTaskAction")
public class ShowShoppingcartTaskAction extends BaseInteractiveTaskAction {
    private Logger logger = LoggerFactory.getLogger(ShowShoppingcartTaskAction.class);

    private final Set<String> intentNames = new HashSet<>(Arrays.asList("order_deal", "pay"));
    private final String SHOPPING_CART_VALUED = "shopping_cart_valued";

    @Override
    protected Message createReplyMessage() {
        String transformIntentName = getPCParamValueOrDefault(PC.TRANSFORM_INTENT_NAME, EntityConstant.NO_INTENT);
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);

        Map<String, String> paramKvs = new HashMap<>();
        switch (intentName) {
            case "order_deal": case "pay" : {
                String shoppingCartInfoJS = getBizDataValue(BC.SHOPPING_CART_INFO);
                String isBeyondDeliveryPrice = getBizDataValueOrDefault(BC.IS_BEYOND_DELIVERY_PRICE, "false");
                if(shoppingCartInfoJS == null){
                    paramKvs.put(SHOPPING_CART_VALUED, "false");                                        //抱歉，您的购物车中没有商品！
                }else if(isBeyondDeliveryPrice != null && isBeyondDeliveryPrice.equals("false")){
                    paramKvs.put(BC.IS_BEYOND_DELIVERY_PRICE, "false");                                 //抱歉，您购买的商品不够起送价！
                }
                break;
            }
            case "select" : {
                String poiName = getBizDataValue(BC.POI_NAME);
                String skuName = getBizDataValue(BC.SKU_NAME);
                paramKvs.put(BC.POI_NAME, poiName);
                paramKvs.put(BC.SKU_NAME, skuName);
                break;
            }
            case "check_shopping_cart" : {
                break;
            }
            case "clear_shopping_cart" : {
                break;
            }
        }
        NlgTemplateInfo nlgTemplateInfo = getNlgTemplateInfoOfDefaultTheme(this.getClass(), transformIntentName, intentName, paramKvs);
        String reply = nlgTemplateInfo.getNlgTemplate();
        return new Message("text", reply);
    }

}
