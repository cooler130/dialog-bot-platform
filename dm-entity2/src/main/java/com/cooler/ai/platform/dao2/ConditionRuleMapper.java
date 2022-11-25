package com.cooler.ai.platform.dao2;

import com.cooler.ai.platform.entity2.ConditionRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ConditionRuleMapper {

    List<ConditionRule> selectByConditionRuleIds(@Param("conditionRuleIds") Set<Integer> conditionRuleIds);

    List<ConditionRule> selectByTaskId(@Param("taskId") Integer taskId);
}