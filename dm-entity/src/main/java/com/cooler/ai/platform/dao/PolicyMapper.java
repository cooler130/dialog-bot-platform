package com.cooler.ai.platform.dao;

import com.cooler.ai.platform.entity.Policy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PolicyMapper {

    List<Policy> selectByStateId(@Param("stateId") Integer currentStateId);
}