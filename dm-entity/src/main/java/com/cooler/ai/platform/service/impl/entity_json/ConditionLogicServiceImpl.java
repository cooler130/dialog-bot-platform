package com.cooler.ai.platform.service.impl.entity_json;

import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.platform.entity.ConditionLogic;
import com.cooler.ai.platform.model.EntityConstant;
import com.cooler.ai.platform.service.entity.ConditionLogicService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("jsonConditionLogicService")
public class ConditionLogicServiceImpl implements ConditionLogicService {

    private static Map<String, String> globalMap = EntityConstant.globalMap;

    @Override
    public List<ConditionLogic> selectByTransitionIds(Set<Integer> transitionIds) {
        List<ConditionLogic> conditionLogics = new ArrayList<>();
        for (Integer transitionId : transitionIds) {
            String json = globalMap.get("ConditionLogic_" + transitionId);
            if(json != null){
                List<ConditionLogic> conditionLogicsGroup = JSONObject.parseArray(json, ConditionLogic.class);
                conditionLogics.addAll(conditionLogicsGroup);
            }
        }
        return conditionLogics;
    }

}
