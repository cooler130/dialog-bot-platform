package com.cooler.ai.dm.service.impl;

import com.cooler.ai.dm.dao.PolicyActionMapper;
import com.cooler.ai.dm.entity.PolicyAction;
import com.cooler.ai.dm.service.PolicyActionService;
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

    @Override
    public Integer insert(PolicyAction policyAction) {
        return policyActionMapper.insert(policyAction);
    }

    @Override
    public PolicyAction getDefaultPolicyAction(String domainName, String taskName, String version) {
        return policyActionMapper.getDefaultPolicyAction(domainName, taskName, version);
    }

}
