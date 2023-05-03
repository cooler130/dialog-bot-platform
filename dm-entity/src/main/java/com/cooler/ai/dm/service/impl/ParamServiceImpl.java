package com.cooler.ai.dm.service.impl;

import com.cooler.ai.dm.dao.ParamMapper;
import com.cooler.ai.dm.entity.Param;
import com.cooler.ai.dm.service.ParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("paramService")
public class ParamServiceImpl implements ParamService {

    @Autowired
    private ParamMapper paramMapper;

    @Override
    public Integer insert(Param param) {
        return paramMapper.insert(param);
    }

    @Override
    public List<Param> getConditionParams(String domainName, String taskName, String paramName, String version) {
        return paramMapper.getConditionParams(domainName, taskName, paramName, version);
    }
}

