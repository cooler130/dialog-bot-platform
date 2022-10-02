package com.cooler.ai.platform.dao;

import com.cooler.ai.platform.entity.Action;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ActionMapper {

    Action getActionById(@Param("actionId") Integer actionId);

    List<Action> selectByActionIds(@Param("actionIds") Set<Integer> actionIds);

    Action getActionByProcessCode(@Param("processCode") String processCode);
}