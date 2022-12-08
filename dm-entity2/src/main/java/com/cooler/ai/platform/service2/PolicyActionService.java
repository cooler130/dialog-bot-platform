package com.cooler.ai.platform.service2;

import com.cooler.ai.platform.entity2.PolicyAction;

import java.util.List;

public interface PolicyActionService {


    List<PolicyAction> getByPolicyId(Integer policyId);

}
