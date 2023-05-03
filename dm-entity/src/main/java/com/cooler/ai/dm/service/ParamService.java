package com.cooler.ai.dm.service;

import com.cooler.ai.dm.entity.Param;

import java.util.List;

public interface ParamService {

    Integer insert(Param param);

    List<Param> getConditionParams(String domainName, String taskName, String paramName, String version);
}
