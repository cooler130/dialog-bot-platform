package com.cooler.ai.platform.service2;

import com.cooler.ai.platform.entity2.PolicyCondition;

import java.util.List;
import java.util.Set;

public interface PolicyConditionService {

    List<PolicyCondition> getPolicyCondition(Set<Integer> policyIds);
}
