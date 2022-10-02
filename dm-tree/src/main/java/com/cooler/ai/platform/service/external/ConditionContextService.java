package com.cooler.ai.platform.service.external;

import com.cooler.ai.platform.entity.ConditionLogic;
import com.cooler.ai.platform.entity.ConditionRule;
import com.cooler.ai.platform.entity.Transition;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DialogState;
import com.cooler.ai.platform.model.ConditionData;

import java.util.Map;

public interface ConditionContextService {

    /**
     * 通过标示获取业务中的被检测数据
     * @param conditionRule
     * @param dialogState
     * @return
     */
    ConditionData getConditionData(Transition transition, ConditionRule conditionRule, ConditionLogic conditionLogic, DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap, boolean isHadBePreprocessed);
}

