package com.cooler.ai.platform.service2;

import com.cooler.ai.platform.entity2.ConditionLogic;

import java.util.List;
import java.util.Set;

public interface ConditionLogicService {
    /**
     * 根据transitionIds查询conditionLogic集合
     * @param transitionIds
     * @return  ConditionLogic集合
     */
    List<ConditionLogic> selectByTransitionIds(Set<Integer> transitionIds);

}
