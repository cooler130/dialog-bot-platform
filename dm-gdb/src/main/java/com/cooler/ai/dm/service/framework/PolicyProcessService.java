package com.cooler.ai.dm.service.framework;

import com.cooler.ai.distribution.facade.model.DMResponse;
import com.cooler.ai.distribution.facade.model.DialogState;
import com.cooler.ai.dm.entity.PolicyAction;

import java.util.List;

public interface PolicyProcessService {

    /**
     * 查询起始动作
     * @param dialogState    DM结构体
     * @return  起始动作
     */
    List<PolicyAction> queryPolicy(DialogState dialogState);

    /**
     * 执行处理动作
     * @param dialogState    DM结构体
     * @param actions   动作集
     * @return  处理动作输出的数据
     */
    DMResponse runActions(DialogState dialogState, List<PolicyAction> actions);


}
