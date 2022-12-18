package com.cooler.ai.dm.service.impl;

import com.cooler.ai.dm.dao.TransformRelationMapper;
import com.cooler.ai.dm.entity.TransformRelation;
import com.cooler.ai.dm.service.TransformRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("transformRelationService")
public class TransformRelationServiceImpl implements TransformRelationService {
    @Autowired
    private TransformRelationMapper transformRelationMapper;

    @Override
    public int insert(TransformRelation record) {
        return transformRelationMapper.insert(record);
    }
}
