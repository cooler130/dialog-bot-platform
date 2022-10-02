package com.cooler.ai.platform.service.impl.entity;

import com.cooler.ai.platform.dao.PolicyMapper;
import com.cooler.ai.platform.entity.Policy;
import com.cooler.ai.platform.service.entity.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("policyService")
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    private PolicyMapper policyMapper;

    @Override
    public List<Policy> selectByIntentStateId(String currentIntentName, Integer currentStateId) {
        List<Policy> policies = policyMapper.selectByStateId(currentStateId);
        List<Policy> targetPolicies = new ArrayList<>();
        A:for (Policy policy : policies) {
            String intentNameAll = policy.getIntentName();
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
