package com.cooler.ai.platform.dao2;

import com.cooler.ai.platform.entity2.Action;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ActionMapper {
    List<Action> selectByActionIds(@Param("actionIds") Set<Integer> actionIds);

    Action getActionByProcessCode(@Param("processCode") String processCode);

    List<Action> getByPolicyId(@Param("policyId") Integer policyId);

}