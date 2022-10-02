package com.cooler.ai.dm.taskaction.process;

import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("getNoScriptTaskAction")
public class GetNoScriptTaskAction extends BaseInnerProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(GetNoScriptTaskAction.class);

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("GetNoScriptTaskAction  （处理动作）不获取任何话术");


        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();

        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "reportScript";
    }
}
