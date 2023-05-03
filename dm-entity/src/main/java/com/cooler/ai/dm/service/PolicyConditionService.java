package com.cooler.ai.dm.service;

import com.cooler.ai.dm.entity.PolicyCondition;

import java.util.List;
import java.util.Set;

public interface PolicyConditionService {

    List<PolicyCondition> getPolicyCondition(Set<Integer> policyIds);

    Integer insert(PolicyCondition policyCondition);
}
