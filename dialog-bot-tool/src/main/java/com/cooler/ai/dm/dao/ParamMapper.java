package com.cooler.ai.dm.dao;

import com.cooler.ai.dm.entity.Param;
import com.cooler.ai.dm.entity.ParamExample;
import java.util.List;

public interface ParamMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Param record);

    int insertSelective(Param record);

    List<Param> selectByExample(ParamExample example);

    Param selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Param record);

    int updateByPrimaryKey(Param record);
}