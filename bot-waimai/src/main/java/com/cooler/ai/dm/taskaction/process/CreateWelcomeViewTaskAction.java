package com.cooler.ai.dm.taskaction.process;

import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("createWelcomeViewTaskAction")
public class CreateWelcomeViewTaskAction extends BaseProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(CreateWelcomeViewTaskAction.class);

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("CreatePreorderTaskAction  （处理动作）创建预览订单");

        //todo：欢迎语可以根据每个人的信息进行定制化，这里使用的是默认的欢迎语，以后可以进行修改

        return null;
    }

    public String routeNextProcessCode() {
       return "showWelcomeView";
    }

}
