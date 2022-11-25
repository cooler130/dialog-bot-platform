package com.cooler.ai.platform.service2;

import com.cooler.ai.platform.entity2.ConditionRule;

import java.util.List;
import java.util.Set;

public interface ConditionRuleService {
    /**
     * 根据checkIds查询Check集合
     * @param conditionRuleIds
     * @return  Check集合
     */
    List<ConditionRule> selectByConditionRuleIds(Set<Integer> conditionRuleIds);

}
