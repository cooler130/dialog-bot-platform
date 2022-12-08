package com.cooler.ai.platform.dao2;

import com.cooler.ai.platform.entity2.PolicyAction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PolicyActionMapper {

    List<PolicyAction> getByPolicyId(@Param("policyId") Integer policyId);

}