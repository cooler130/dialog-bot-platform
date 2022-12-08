package com.cooler.ai.platform.dao2;

import com.cooler.ai.platform.entity2.TransformRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TransformRelationMapper {

    List<TransformRelation> selectByDTS(@Param("domainName") String domainName, @Param("taskName") String taskName, @Param("contextState") String contextState);
}