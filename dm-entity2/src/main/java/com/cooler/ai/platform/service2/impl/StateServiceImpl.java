package com.cooler.ai.platform.service2.impl;

import com.cooler.ai.platform.entity2.State;
import com.cooler.ai.platform.dao2.StateMapper;
import com.cooler.ai.platform.service2.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("stateService")
public class StateServiceImpl implements StateService {

    @Autowired
    private StateMapper stateMapper;

    @Override
    public State selectByStateId(Integer currentStateId) {
        return stateMapper.selectByStateId(currentStateId);
    }
}
