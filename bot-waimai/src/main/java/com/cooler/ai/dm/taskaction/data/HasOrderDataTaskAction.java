package com.cooler.ai.dm.taskaction.data;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.action.BaseDataTaskAction;
import com.cooler.ai.platform.facade.BizDataFacade;
import com.cooler.ai.platform.facade.constance.CC;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.model.WaimaiConstant;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2019/1/3
 **/
@Component("hasOrderDataTaskAction")
public class HasOrderDataTaskAction extends BaseDataTaskAction {

    @Resource
    private BizDataFacade bizDataFacade;

    @Override
    public void preprocess() {
        String userId = getPCParamValue(CC.USER_ID);

        String intentName = getPCParamValue(PC.INTENT_NAME);

        String currentOrderPageNumStr = getBizDataValueOrDefault(BC.CURRENT_ORDER_PAGE_NUM, "0");
        int currentOrderPageNum = Integer.parseInt(currentOrderPageNumStr);

        String stateStr = getBizDataValueOrDefault(BC.CURRENT_ORDER_STATE, WaimaiConstant.ALL + "");
        int state = Integer.parseInt(stateStr);

        switch (intentName) {
            case "want_anotherorder" : case "inquire_order" : case "restart" : {
                currentOrderPageNum = 0;                        //初次查询订单或者重新开始查询，从第0页开始
                state = WaimaiConstant.ALL;                     //初次查询订单或者重新开始查询，state 直接为WaimaiConstant.ALL（默认查询所有订单）
                break;
            }
            case "turn_page" : {
                //翻到某一页，使用currentOrderPageNum
                //翻到某一页，使用上文传入的orderState
                break;
            }
            case "control_want_more" : {
                currentOrderPageNum ++;
                //使用上文传入的orderState
                break;
            }
            case "search_other_order" : {
                currentOrderPageNum = 0;
                stateStr = dialogState.getParamValue(BC.CURRENT_ORDER_STATE, Constant.SLOT_PARAM);
                state = Integer.parseInt(stateStr);
                break;
            }
        }

        Map<String, String> orderDataInfosMap = bizDataFacade.getOrderDataByUserIdStatePageNum(userId, state, currentOrderPageNum);
        if(orderDataInfosMap != null){
            addToParamValueAndBizDataMap(BC.ORDER_DATA_INFOS_MAP, JSON.toJSONString(orderDataInfosMap), Constant.BIZ_PARAM, Constant.ZERO);
        }
    }

    @Override
    public String getParamValue() {
        String orderDataInfosMapJS = getBizDataValue(BC.ORDER_DATA_INFOS_MAP);
        if(orderDataInfosMapJS != null){
            Map<String, String> orderDataInfosMap = JSON.parseObject(orderDataInfosMapJS, Map.class);
            if(orderDataInfosMap != null && orderDataInfosMap.size() > 0){
                String orderCountThisPageStr = orderDataInfosMap.get(WaimaiConstant.ORDER_COUNT_THIS_PAGE);
                int orderCountThisPage = Integer.parseInt(orderCountThisPageStr);
                if(orderCountThisPage > 0) return "true";
            }
        }
        return "false";
    }

}
