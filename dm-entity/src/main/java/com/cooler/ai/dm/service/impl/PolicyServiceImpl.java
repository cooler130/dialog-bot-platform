package com.cooler.ai.dm.service.impl;

import com.cooler.ai.dm.service.PolicyService;
import com.cooler.ai.dm.dao.PolicyMapper;
import com.cooler.ai.dm.entity.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("policyService")
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    private PolicyMapper policyMapper;

    @Override
    public List<Policy> selectByFromToState(String domainName, String taskName, String fromState, String currentIntentName, String toState, String version) {
        List<Policy> policies = policyMapper.selectByFromToState(domainName, taskName, fromState, toState, version);
        List<Policy> targetPolicies = new ArrayList<>();
        A:for (Policy policy : policies) {
            String intentNameAll = policy.getIntentNames();
            String[] intentNames = intentNameAll.split(",");
            B:for (String intentName : intentNames) {
                intentName = intentName.trim();
                if(intentName.equals(currentIntentName)){
                    targetPolicies.add(policy);
                    break B;
                }
            }
        }
        return targetPolicies;
    }

    @Override
    public void insert(Policy policy) {
        policyMapper.insert(policy);
    }

    @Override
    public Policy selectDefaultPolicy(String domainName, String taskName, String version) {
        return policyMapper.selectDefaultPolicy(domainName, taskName, version);
    }
}
