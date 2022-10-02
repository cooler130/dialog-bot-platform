package com.cooler.ai.platform.service.entity;

import com.cooler.ai.platform.entity.Policy;

import java.util.List;

public interface PolicyService {

    /**
     * 根据状态ID查询策略集
     * @param currentIntentName
     * @param currentStateId
     * @return  策略集
     */
    List<Policy> selectByIntentStateId(String currentIntentName, Integer currentStateId);
}
