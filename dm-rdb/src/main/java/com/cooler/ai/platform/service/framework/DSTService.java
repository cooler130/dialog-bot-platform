package com.cooler.ai.platform.service.framework;

import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DialogState;

import java.util.Map;

public interface DSTService {

    void fsmDSTProcess(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap);

}
