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
@Component("timeoutCountDataTaskAction")
public class TimeoutCountDataTaskAction extends BaseDataTaskAction {
    private Logger logger = LoggerFactory.getLogger(TimeoutCountDataTaskAction.class);

    @Override
    public String getParamValue() {
        String timeoutCountStr = getBizDataValueOrDefault(BC.TIMEOUT_COUNT, "0");
        return timeoutCountStr;
    }


}
