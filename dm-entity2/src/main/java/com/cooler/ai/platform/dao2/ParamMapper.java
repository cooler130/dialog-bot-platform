package com.cooler.ai.platform.dao2;

import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ParamMapper {

    List<com.cooler.ai.platform.entity2.Param> getConditionParams(@Param("domainName") String domainName,
                                                                  @Param("taskName") String taskName,
                                                                  @Param("paramName") String paramName);

}