package com.cooler.ai.dm.taskaction.interact;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.platform.action.BaseInteractiveTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("showPoiListTaskAction")
public class ShowPoiListTaskAction extends BaseInteractiveTaskAction {

    private Logger logger = LoggerFactory.getLogger(ShowPoiListTaskAction.class);
    private final String POI_NAME_VALUED = "poi_name_valued";

    @Override
    protected Message createReplyMessage() {
        String searchPoiParamsJS = getBizDataValue(BC.SEARCH_POI_PARAMS);
        Map<String, String> searchPoiParams = JSON.parseObject(searchPoiParamsJS, Map.class);

        //下面展示的值是前面用到的值
        String poiName = null;
        if(searchPoiParams != null){
            poiName = searchPoiParams.get(BC.POI_NAME);
        }

        //构建话术（槽位值替换变量）
        Map<String, String> paramKvs = new HashMap<>();
        String reply = null;
        if(poiName != null && poiName.length() > 0){
            paramKvs.put(POI_NAME_VALUED, "true");
            paramKvs.put(BC.POI_NAME, poiName);                                                 //为您找到如下${poi_name}
        }else{
            paramKvs.put(POI_NAME_VALUED, "false");                                           //为您找到如下商家！
        }
        NlgTemplateInfo nlgTemplateInfo = getNlgTemplateInfoOfDefaultTheme(this.getClass(), Constant.NONE_VALUE, Constant.NONE_VALUE, paramKvs);
        reply = nlgTemplateInfo.getNlgTemplate();

        return new Message("text", reply);
    }

}
