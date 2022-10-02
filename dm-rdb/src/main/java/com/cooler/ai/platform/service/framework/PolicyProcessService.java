package com.cooler.ai.platform.service.framework;

import com.cooler.ai.platform.entity.Action;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DMResponse;
import com.cooler.ai.platform.facade.model.DialogState;

import java.util.Map;

public interface PolicyProcessService {

    /**
     * 查询起始动作
     * @param dialogState    DM结构体
     * @return  起始动作
     */
    Action queryPolicy(DialogState dialogState);

    /**
     * 执行处理动作
     * @param dialogState    DM结构体
     * @param startAction   处理动作集
     * @return  处理动作输出的数据
     */
    DMResponse runActions(DMRequest dmRequest, DialogState dialogState, Action startAction, Map<String, BizDataModelState<String>> bizDataMap);


}
