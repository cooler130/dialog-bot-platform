package com.cooler.ai.platform.service2.impl;

import com.cooler.ai.platform.dao2.ConditionRuleMapper;
import com.cooler.ai.platform.entity2.ConditionRule;
import com.cooler.ai.platform.service2.ConditionRuleService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("conditionRuleService")
public class ConditionRuleServiceImpl implements ConditionRuleService {

    @Autowired
    private ConditionRuleMapper conditionRuleMapper;

    @Override
    public List<ConditionRule> selectByConditionRuleIds(@Param("conditionRuleIds") Set<Integer> conditionRuleIds) {
        return conditionRuleMapper.selectByConditionRuleIds(conditionRuleIds);
    }

}
