package com.cooler.ai.dm.dao;

import com.cooler.ai.dm.entity.PolicyAction;
import com.cooler.ai.dm.entity.PolicyActionExample;
import java.util.List;

public interface PolicyActionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PolicyAction record);

    int insertSelective(PolicyAction record);

    List<PolicyAction> selectByExample(PolicyActionExample example);

    PolicyAction selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PolicyAction record);

    int updateByPrimaryKey(PolicyAction record);
}