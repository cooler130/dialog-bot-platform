package com.cooler.ai.platform.dao2;

import com.cooler.ai.platform.entity2.Policy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PolicyMapper {
    List<Policy> selectByStateId(@Param("stateId") Integer currentStateId);

    List<Policy> selectByFromToState(@Param("domainName")String domainName, @Param("taskName")String taskName, @Param("fromState")String fromState, @Param("toState")String toState);

}