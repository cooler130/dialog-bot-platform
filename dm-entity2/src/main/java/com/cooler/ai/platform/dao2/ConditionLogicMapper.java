package com.cooler.ai.platform.dao2;

import com.cooler.ai.platform.entity2.ConditionLogic;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ConditionLogicMapper {
    List<ConditionLogic> selectByTransitionIds(@Param("transitionIds") Set<Integer> transitionIds);

}