package com.cooler.ai.platform.service2.impl;

import com.cooler.ai.platform.dao2.PolicyActionMapper;
import com.cooler.ai.platform.entity2.PolicyAction;
import com.cooler.ai.platform.service2.PolicyActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("policyActionService")
public class PolicyActionServiceImpl implements PolicyActionService {

    @Autowired
    private PolicyActionMapper policyActionMapper;


    @Override
    public List<PolicyAction> getByPolicyId(Integer policyId) {
        return policyActionMapper.getByPolicyId(policyId);
    }

}
