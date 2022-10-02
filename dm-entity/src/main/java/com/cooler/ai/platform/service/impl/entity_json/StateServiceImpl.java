package com.cooler.ai.platform.service.impl.entity_json;

import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.platform.entity.State;
import com.cooler.ai.platform.model.EntityConstant;
import com.cooler.ai.platform.service.entity.StateService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("jsonStateService")
public class StateServiceImpl implements StateService {

    private static Map<String, String> globalMap = EntityConstant.globalMap;

    @Override
    public State selectByStateId(Integer currentStateId) {
        State state = null;
        String json = globalMap.get("State_" + currentStateId);
        if(json != null){
            state = JSONObject.parseObject(json, State.class);
        }
        return state;
    }
}
