package com.cooler.ai.platform.dao2;

import com.cooler.ai.platform.entity2.Transition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TransitionMapper {

    List<Transition> selectByTaskStartStateId(@Param("taskName") String taskName, @Param("currentStateId") Integer currentStateId);

    List<Transition> selectByTaskName(@Param("taskName") String taskName);

}