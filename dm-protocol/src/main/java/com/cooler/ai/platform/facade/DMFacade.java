package com.cooler.ai.platform.facade;

import com.cooler.ai.platform.facade.model.*;

import java.util.Map;

public interface DMFacade {

    DMResponse process(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap);

}
