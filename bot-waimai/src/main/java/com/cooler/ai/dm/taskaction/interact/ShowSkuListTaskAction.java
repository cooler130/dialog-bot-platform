package com.cooler.ai.dm.taskaction.interact;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.platform.action.BaseInteractiveTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.Message;
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
@Component("showSkuListTaskAction")
public class ShowSkuListTaskAction extends BaseInteractiveTaskAction {
    private Logger logger = LoggerFactory.getLogger(ShowSkuListTaskAction.class);

    private Set<String> payIntentNames = new HashSet<>(Arrays.asList("pay", "order_deal"));

    private final String POI_NAME_VALUED = "poi_name_valued";
    private final String SKU_INFOS_VALUED = "sku_infos_valued";

    @Override
    protected Message createReplyMessage() {
        //准备槽位值
        String poiName = getBizDataValue(BC.POI_NAME);
        String skuInfosJS = getBizDataValue(BC.SKU_INFOS);
        List<SkuInfo> skuInfos = JSON.parseArray(skuInfosJS, SkuInfo.class);

        boolean poiNameValued = poiName != null && poiName.length() > 0;
        boolean skuInfosValued = skuInfos != null && skuInfos.size() > 0;

        //构建话术（槽位值替换变量）
        Map<String, String> paramKvs1 = new HashMap<>();
        paramKvs1.put(BC.POI_NAME, poiName);
        paramKvs1.put(POI_NAME_VALUED, poiNameValued ? "true" : "false");            //true：当前在${poi_name}中； false：下面可能您会喜欢；

        NlgTemplateInfo nlgTemplateInfo = getNlgTemplateInfoOfDefaultTheme(this.getClass(), Constant.NONE_VALUE, Constant.NONE_VALUE, paramKvs1);
        String poiReply = nlgTemplateInfo.getNlgTemplate();

        Map<String, String> paramKvs2 = new HashMap<>();
        paramKvs2.put(SKU_INFOS_VALUED, skuInfosValued ? "true" : "false");          //true：搜索到如下商品； false：当前没有搜索到商品；
        NlgTemplateInfo nlgTemplateInfo2 = getNlgTemplateInfoOfDefaultTheme(this.getClass(), Constant.NONE_VALUE, Constant.NONE_VALUE, paramKvs2);
        String skuReply = nlgTemplateInfo2.getNlgTemplate();

        return new Message("text", poiReply + skuReply);
    }

    @Override
    protected Message createOtherMessage() {
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);
        if(payIntentNames.contains(intentName)){
            Map<String, String> paramKvs = new HashMap<>();
            paramKvs.put(BC.IS_BEYOND_DELIVERY_PRICE, "false");
            NlgTemplateInfo nlgTemplateInfo = getNlgTemplateInfoOfDefaultTheme(this.getClass(), Constant.NONE_VALUE, Constant.NONE_VALUE, paramKvs);//不能去下单哦，还不够起送价，请回到商家，再加点东西吧！
            String reply = nlgTemplateInfo.getNlgTemplate();

            return new Message("bubble", reply);
        }
        return null;
    }
}
