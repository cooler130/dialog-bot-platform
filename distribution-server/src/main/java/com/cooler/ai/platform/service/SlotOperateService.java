package com.cooler.ai.platform.service;

import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DialogState;

import java.util.List;
import java.util.Map;

public interface SlotOperateService {

    /**
     * 对槽位值的操作，具体可分为如下方法实现的过程：
     * @param dmRequest     DM请求体
     * @param dialogStates  DS结构体
     * @param bizDataMSMap  业务数据
     * @return
     */
    DialogState operationSlots(DMRequest dmRequest, List<DialogState> dialogStates, Map<String, BizDataModelState<String>> bizDataMSMap);

}
