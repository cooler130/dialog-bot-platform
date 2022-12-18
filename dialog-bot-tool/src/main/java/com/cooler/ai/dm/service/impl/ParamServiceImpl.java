package com.cooler.ai.dm.service.impl;

import com.cooler.ai.dm.dao.ParamMapper;
import com.cooler.ai.dm.entity.Param;
import com.cooler.ai.dm.service.ParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("paramService")
public class ParamServiceImpl implements ParamService {
    @Autowired
    private ParamMapper paramMapper;

    @Override
    public int insert(Param record) {
        return paramMapper.insert(record);
    }
}
