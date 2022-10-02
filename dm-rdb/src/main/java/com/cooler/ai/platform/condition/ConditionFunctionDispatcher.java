package com.cooler.ai.platform.condition;

import java.util.Map;

public class ConditionFunctionDispatcher {

    private Map<String, ConditionFunction> checkFunctionMap = null;

    public Map<String, ConditionFunction> getCheckFunctionMap() {
        return checkFunctionMap;
    }

    public void setCheckFunctionMap(Map<String, ConditionFunction> checkFunctionMap) {
        this.checkFunctionMap = checkFunctionMap;
    }

    public ConditionFunction getFunction(String checkFunctionCode) {
        ConditionFunction conditionFunction = checkFunctionMap.get(checkFunctionCode);
        return conditionFunction;
    }
}
