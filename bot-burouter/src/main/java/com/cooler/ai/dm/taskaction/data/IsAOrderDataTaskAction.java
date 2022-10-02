package com.cooler.ai.dm.taskaction.data;

import com.cooler.ai.platform.action.BaseDataTaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2019/1/3
 **/
@Component("isAOrderDataTaskAction")
public class IsAOrderDataTaskAction extends BaseDataTaskAction {
    private Logger logger = LoggerFactory.getLogger(IsAOrderDataTaskAction.class);

    @Override
    public String getParamValue() {

        return "true";
    }

}
