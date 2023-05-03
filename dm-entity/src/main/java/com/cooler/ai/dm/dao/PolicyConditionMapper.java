package com.cooler.ai.dm.dao;

import com.cooler.ai.dm.entity.PolicyCondition;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface PolicyConditionMapper {
    List<PolicyCondition> getConditionByPolicyId(@Param("policyIds") Set<Integer> policyIds);

    Integer insert(PolicyCondition policyCondition);
}