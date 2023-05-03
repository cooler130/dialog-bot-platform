package com.cooler.ai.dm.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ParamMapper {

    List<com.cooler.ai.dm.entity.Param> getConditionParams(@Param("domainName") String domainName,
                                                           @Param("taskName") String taskName,
                                                           @Param("paramName") String paramName,
                                                           @Param("version") String version);

    Integer insert(com.cooler.ai.dm.entity.Param param);
}