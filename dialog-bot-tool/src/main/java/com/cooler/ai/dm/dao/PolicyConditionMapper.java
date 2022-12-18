package com.cooler.ai.dm.dao;

import com.cooler.ai.dm.entity.PolicyCondition;
import com.cooler.ai.dm.entity.PolicyConditionExample;
import java.util.List;

public interface PolicyConditionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PolicyCondition record);

    int insertSelective(PolicyCondition record);

    List<PolicyCondition> selectByExample(PolicyConditionExample example);

    PolicyCondition selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PolicyCondition record);

    int updateByPrimaryKey(PolicyCondition record);
}