package com.cooler.ai.platform.dao;

import com.cooler.ai.platform.entity.ConditionKV;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ConditionKVMapper {

    /**
     * 根据policyIds查询对应的ConditionKV集合
     * @param policyIds
     * @return
     */
    List<ConditionKV> getConditionKVsByPolicyIds(@Param("policyIds") Set<Integer> policyIds);

}