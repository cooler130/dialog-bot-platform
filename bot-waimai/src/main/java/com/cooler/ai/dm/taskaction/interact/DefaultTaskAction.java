package com.cooler.ai.dm.taskaction.interact;

import com.cooler.ai.nlg.NlgConstant;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
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
        NlgTemplateInfo nlgTemplateInfo = getNlgTemplateInfoOfDefaultTheme(this.getClass(), NlgConstant.V_NONE_VALUE, NlgConstant.V_NONE_VALUE, null);
        String nlgSentence = nlgTemplateInfo.getNlgTemplate();
        return new Message("text", nlgSentence);
    }
}
