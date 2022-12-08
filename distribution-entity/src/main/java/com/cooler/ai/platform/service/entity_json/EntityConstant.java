package com.cooler.ai.platform.service.entity_json;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityConstant {
    public static final Map<String, String> globalMap = new ConcurrentHashMap<>();  //为了支持以后的热加载，还是用ConcurrentHashMap

}
