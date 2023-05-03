package com.cooler.ai.dm.service;

import com.cooler.ai.dm.entity.Policy;

import java.util.List;

public interface PolicyService {

    /**
     * 根据众多参数查询出策略集合
     * @param domainName
     * @param taskName
     * @param fromState
     * @param intentName
     * @param toState
     * @param version
     * @return
     */
    List<Policy> selectByFromToState(String domainName,
                                     String taskName,
                                     String fromState,
                                     String intentName,
                                     String toState,
                                     String version);

    void insert(Policy policy);

    Policy selectDefaultPolicy(String domainName,
                               String taskName,
                               String version);
}
