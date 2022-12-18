package com.cooler.ai.dm.dao;

import com.cooler.ai.dm.entity.TransformRelation;
import com.cooler.ai.dm.entity.TransformRelationExample;
import java.util.List;

public interface TransformRelationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TransformRelation record);

    int insertSelective(TransformRelation record);

    List<TransformRelation> selectByExample(TransformRelationExample example);

    TransformRelation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TransformRelation record);

    int updateByPrimaryKey(TransformRelation record);
}