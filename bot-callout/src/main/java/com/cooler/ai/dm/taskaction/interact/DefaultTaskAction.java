package com.cooler.ai.dm.taskaction.interact;

import com.cooler.ai.platform.action.BaseInteractiveTaskAction;
import com.cooler.ai.platform.facade.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("defaultTaskAction")
public class DefaultTaskAction extends BaseInteractiveTaskAction {
    private Logger logger = LoggerFactory.getLogger(DefaultTaskAction.class);

    @Override
    protected Message createReplyMessage() {
        return new Message("text", "抱歉，没有理解您说的话！");
    }
}
