package com.cooler.ai.dm.dao;

import com.cooler.ai.dm.entity.TransformRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TransformRelationMapper {

    List<TransformRelation> selectByDTS(@Param("domainName") String domainName,
                                        @Param("taskName") String taskName,
                                        @Param("contextState") String contextState,
                                        @Param("version") String version);
}