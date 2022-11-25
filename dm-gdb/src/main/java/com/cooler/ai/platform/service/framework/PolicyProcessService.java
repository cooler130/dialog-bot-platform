package com.cooler.ai.platform.service.framework;

import com.cooler.ai.platform.entity2.Action;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DMResponse;
import com.cooler.ai.platform.facade.model.DialogState;

import java.util.List;
import java.util.Map;

public interface PolicyProcessService {

    /**
     * 查询起始动作
     * @param dialogState    DM结构体
     * @return  起始动作
     */
    List<Action> queryPolicy(DialogState dialogState);

    /**
     * 执行处理动作
     * @param dialogState    DM结构体
     * @param actions   动作集
     * @return  处理动作输出的数据
     */
    DMResponse runActions(DMRequest dmRequest, DialogState dialogState, List<Action> actions, Map<String, BizDataModelState<String>> bizDataMap);


}
