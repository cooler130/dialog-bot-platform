package com.cooler.ai.dm.taskaction.interact;

import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.action.BaseInteractiveTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("reportScriptTaskAction")
public class ReportScriptTaskAction extends BaseInteractiveTaskAction {
    private Logger logger = LoggerFactory.getLogger(ReportScriptTaskAction.class);

    @Override
    protected Message createReplyMessage() {
//        String script = getBizDataValue(BC.SCRIPT);
//        Message message = new Message(Constant.MSG_TEXT, script);
//        return message;
        return null;
    }
}
