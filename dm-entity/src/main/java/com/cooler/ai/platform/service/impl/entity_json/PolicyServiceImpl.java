package com.cooler.ai.platform.service.impl.entity_json;

import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.platform.entity.Policy;
import com.cooler.ai.platform.model.EntityConstant;
import com.cooler.ai.platform.service.entity.PolicyService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("jsonPolicyService")
public class PolicyServiceImpl implements PolicyService {

    private static Map<String, String> globalMap = EntityConstant.globalMap;

    @Override
    public List<Policy> selectByIntentStateId(String currentIntentName, Integer currentStateId) {
        List<Policy> policies = new ArrayList<>();
        String json = globalMap.get("Policy_" + currentIntentName + "_" + currentStateId);
        if(json != null){
            policies = JSONObject.parseArray(json, Policy.class);
        }
        return policies;
    }
}
