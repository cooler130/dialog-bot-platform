package com.cooler.ai.distribution.service.entity_json;

import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.distribution.entity.Intent;
import com.cooler.ai.distribution.service.IntentService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("jsonIntentService")
public class IntentServiceImpl implements IntentService {

    private static Map<String, String> globalMap = EntityConstant.globalMap;

    @Override
    public Intent selectByIntentId(Integer intentId) {
        Intent intent = null;
        String json = globalMap.get("Intent_" + intentId);
        if(json != null){
            intent = JSONObject.parseObject(json, Intent.class);
        }
        return intent;
    }

    @Override
    public Intent selectByTwoNames(String domainName, String intentName) {
        Intent intent = null;
        String json = globalMap.get("Intent_" + domainName + "_" + intentName);
        if(json != null){
            intent = JSONObject.parseObject(json, Intent.class);
        }
        return intent;
    }

    @Override
    public void insert(Intent intent) {
        globalMap.put("Intent_" + intent.getDomainName() + "_" + intent.getIntentName(), JSONObject.toJSONString(intent));
    }

    @Override
    public Integer selectMaxId() {
        return null;
    }
}
