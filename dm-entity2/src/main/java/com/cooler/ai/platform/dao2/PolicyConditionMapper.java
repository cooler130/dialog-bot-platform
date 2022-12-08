package com.cooler.ai.platform.dao2;

import com.cooler.ai.platform.entity2.PolicyCondition;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface PolicyConditionMapper {
    List<PolicyCondition> getConditionByPolicyId(@Param("policyIds") Set<Integer> policyIds);
}