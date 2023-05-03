package com.cooler.ai.dm.service;

import com.cooler.ai.dm.entity.PolicyAction;

import java.util.List;

public interface PolicyActionService {

    List<PolicyAction> getByPolicyId(Integer policyId);

    Integer insert(PolicyAction policyAction);

    PolicyAction getDefaultPolicyAction(String domainName,
                                        String taskName,
                                        String version);


}
