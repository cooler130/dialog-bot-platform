package com.cooler.ai.platform.service.entity;

import com.cooler.ai.platform.entity.Action;

import java.util.List;
import java.util.Set;

public interface ActionService {

    /**
     * 通过Action的ID集合，查找Action集合
     * @param actionIds
     * @return  Action对象
     */
    List<Action> selectByActionIds(Set<Integer> actionIds);

    Action getActionById(Integer actionId);

    Action getActionByProcessCode(String processCode);
}
