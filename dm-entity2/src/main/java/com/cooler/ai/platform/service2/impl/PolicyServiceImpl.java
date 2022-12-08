package com.cooler.ai.platform.service2.impl;

import com.cooler.ai.platform.dao2.PolicyMapper;
import com.cooler.ai.platform.entity2.Policy;
import com.cooler.ai.platform.service2.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("policyService")
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    private PolicyMapper policyMapper;

    @Override
    public List<Policy> selectByFromToState(String domainName, String taskName, String fromState, String currentIntentName, String toState) {
        List<Policy> policies = policyMapper.selectByFromToState(domainName, taskName, fromState, toState);
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
}
