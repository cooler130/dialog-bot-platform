package com.cooler.ai.platform.dao2;

import com.cooler.ai.platform.entity2.State;
import org.apache.ibatis.annotations.Param;

public interface StateMapper {

    State selectByStateId(@Param("stateId") Integer currentStateId);
}