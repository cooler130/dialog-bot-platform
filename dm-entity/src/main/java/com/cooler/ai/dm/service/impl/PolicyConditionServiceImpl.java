package com.cooler.ai.dm.service.impl;

import com.cooler.ai.dm.service.PolicyConditionService;
import com.cooler.ai.dm.dao.PolicyConditionMapper;
import com.cooler.ai.dm.entity.PolicyCondition;
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

    @Override
    public Integer insert(PolicyCondition policyCondition) {
        return policyConditionMapper.insert(policyCondition);
    }
}
