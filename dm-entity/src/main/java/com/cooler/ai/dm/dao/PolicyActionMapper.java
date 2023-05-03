package com.cooler.ai.dm.dao;

import com.cooler.ai.dm.entity.PolicyAction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PolicyActionMapper {

    List<PolicyAction> getByPolicyId(@Param("policyId") Integer policyId);

    Integer insert(PolicyAction policyAction);

    PolicyAction getDefaultPolicyAction(@Param("domainName") String domainName,
                                        @Param("taskName") String taskName,
                                        @Param("version") String version);

}