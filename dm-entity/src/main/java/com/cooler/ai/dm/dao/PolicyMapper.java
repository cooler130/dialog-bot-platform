package com.cooler.ai.dm.dao;

import com.cooler.ai.dm.entity.Policy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PolicyMapper {
    List<Policy> selectByFromToState(@Param("domainName")String domainName,
                                     @Param("taskName")String taskName,
                                     @Param("fromState")String fromState,
                                     @Param("toState")String toState,
                                     @Param("version")String version);

    void insert(Policy policy);

    Policy selectDefaultPolicy(@Param("domainName")String domainName,
                               @Param("taskName")String taskName,
                               @Param("version")String version);


}