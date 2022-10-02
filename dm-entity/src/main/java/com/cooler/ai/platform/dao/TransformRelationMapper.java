package com.cooler.ai.platform.dao;

import com.cooler.ai.platform.entity.TransformRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TransformRelationMapper {

    List<TransformRelation> selectByContextStateId(@Param("contextStateId") Integer contextStateId);

}