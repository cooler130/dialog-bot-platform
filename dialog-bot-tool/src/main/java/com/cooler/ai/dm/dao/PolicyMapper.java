package com.cooler.ai.dm.dao;

import com.cooler.ai.dm.entity.Policy;
import com.cooler.ai.dm.entity.PolicyExample;
import java.util.List;

public interface PolicyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Policy record);

    int insertSelective(Policy record);

    List<Policy> selectByExample(PolicyExample example);

    Policy selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Policy record);

    int updateByPrimaryKey(Policy record);
}