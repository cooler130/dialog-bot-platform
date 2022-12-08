package com.cooler.ai.platform.service2;

import com.cooler.ai.platform.entity2.Param;

import java.util.List;

public interface ParamService {

    List<Param> getConditionParams(String domainName, String taskName, String paramName);
}
