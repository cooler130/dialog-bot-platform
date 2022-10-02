package com.cooler.ai.dm.taskaction.data;

import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.action.BaseDataTaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2019/1/3
 **/
@Component("orderCountDataTaskAction")
public class OrderCountDataTaskAction extends BaseDataTaskAction {
    private Logger logger = LoggerFactory.getLogger(OrderCountDataTaskAction.class);

    @Override
    public String getParamValue() {
        String orderCountStr = getBizDataValueOrDefault(BC.ORDER_COUNT, "0");
        return orderCountStr;
    }

}
