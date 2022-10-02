package com.cooler.ai.platform.dao;

import com.cooler.ai.platform.entity.State;
import org.apache.ibatis.annotations.Param;

public interface StateMapper {

    State selectByStateId(@Param("stateId") Integer currentStateId);
}