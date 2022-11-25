package com.cooler.ai.platform.service2;

import com.cooler.ai.platform.entity2.Policy;

import java.util.List;

public interface PolicyService {

    /**
     * 根据状态ID查询策略集
     * @param currentIntentName
     * @param currentStateId
     * @return  策略集
     */
    List<Policy> selectByIntentStateId(String currentIntentName, Integer currentStateId);

    /**
     * 根据众多参数查询出策略集合
     * @param domainName
     * @param taskName
     * @param fromState
     * @param intentName
     * @param toState
     * @return
     */
    List<Policy> selectByFromToState(String domainName, String taskName, String fromState, String intentName, String toState);

}
