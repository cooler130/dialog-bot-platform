package com.cooler.ai.platform.service.entity;

import com.cooler.ai.platform.entity.ConditionKV;

import java.util.List;
import java.util.Set;


public interface ConditionKVService {

    List<ConditionKV> getConditionKVsByPolicyIds(Set<Integer> policyIds);
}
