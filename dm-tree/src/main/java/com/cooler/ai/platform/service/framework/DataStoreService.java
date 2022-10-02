package com.cooler.ai.platform.service.framework;

import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DialogState;

import java.util.Map;

public interface DataStoreService {

    /**
     * 保存数据，分为缓存数据，以及保存日志
     * @param dmRequest      DM请求体
     * @param dialogState    DM结构体
     * @param bizDataMap     业务数据（每一个action运行过程中产生的）
     */
    void storeData(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMap);

}
