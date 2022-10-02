package com.cooler.ai.platform.service.entity;

import com.cooler.ai.platform.entity.ConditionLogic;

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
