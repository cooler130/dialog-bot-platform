package com.cooler.ai.distribution.facade;

import com.cooler.ai.distribution.facade.model.BizDataModelState;
import com.cooler.ai.distribution.facade.model.DMRequest;
import com.cooler.ai.distribution.facade.model.DMResponse;
import com.cooler.ai.distribution.facade.model.DialogState;

import java.util.Map;

public interface DMFacade {

    DMResponse process(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap);

}
