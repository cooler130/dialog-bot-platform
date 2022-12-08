package com.cooler.ai.platform.service2.impl;

import com.cooler.ai.platform.dao2.PolicyConditionMapper;
import com.cooler.ai.platform.entity2.PolicyCondition;
import com.cooler.ai.platform.service2.PolicyConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("policyConditionService")
public class PolicyConditionServiceImpl implements PolicyConditionService {

    @Autowired
    private PolicyConditionMapper policyConditionMapper;

    @Override
    public List<PolicyCondition> getPolicyCondition(Set<Integer> policyIds) {
        return policyConditionMapper.getConditionByPolicyId(policyIds);
    }
}
