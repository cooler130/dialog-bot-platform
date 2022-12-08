package com.cooler.ai.platform.service2.impl;

import com.cooler.ai.platform.dao2.ParamMapper;
import com.cooler.ai.platform.entity2.Param;
import com.cooler.ai.platform.service2.ParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("paramService")
public class ParamServiceImpl implements ParamService {

    @Autowired
    private ParamMapper paramMapper;

    @Override
    public List<Param> getConditionParams(String domainName, String taskName, String paramName) {
        return paramMapper.getConditionParams(domainName, taskName, paramName);
    }
}

