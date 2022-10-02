package com.cooler.ai.dm.taskaction.interact;

import com.cooler.ai.dm.constant.BC;
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
@Component("showFaqAnswerTaskAction")
public class ShowFaqAnswerTaskAction extends BaseInteractiveTaskAction {
    private Logger logger = LoggerFactory.getLogger(ShowFaqAnswerTaskAction.class);

    @Override
    protected Message createReplyMessage() {
        String faqAnswer = getBizDataValue(BC.FAQ_ANSWER);
        return new Message("text", faqAnswer);
    }

}
