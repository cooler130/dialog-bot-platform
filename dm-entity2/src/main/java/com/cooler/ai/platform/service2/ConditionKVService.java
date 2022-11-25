package com.cooler.ai.platform.service2;

import com.cooler.ai.platform.entity2.ConditionKV;

import java.util.List;
import java.util.Set;


public interface ConditionKVService {

    List<ConditionKV> getConditionKVsByPolicyIds(Set<Integer> policyIds);
}
