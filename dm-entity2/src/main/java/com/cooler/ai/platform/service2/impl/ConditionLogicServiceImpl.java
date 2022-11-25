package com.cooler.ai.platform.service2.impl;

import com.cooler.ai.platform.dao2.ConditionLogicMapper;
import com.cooler.ai.platform.entity2.ConditionLogic;
import com.cooler.ai.platform.service2.ConditionLogicService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service("conditionLogicService")
public class ConditionLogicServiceImpl implements ConditionLogicService {
    @Autowired
    private ConditionLogicMapper conditionLogicMapper;

    @Override
    public List<ConditionLogic> selectByTransitionIds(@Param("transitionIds") Set<Integer> transitionIds) {
        return conditionLogicMapper.selectByTransitionIds(transitionIds);
    }

}
