package com.cooler.ai.platform.model;

import com.cooler.ai.platform.entity.ConditionLogic;
import com.cooler.ai.platform.entity.ConditionRule;

import java.util.Map;

public interface ConditionData<V> {

    ConditionRule getConditionRule();

    ConditionLogic getConditionLogic();

    Integer getParamType();

    String getParamsName();

    V getValue();

    float getBelief();

    Boolean getResult();

    void setResult(Boolean result);

    boolean isNecessary();                      //如果此数据为槽位变量下的值才有意义，标示是否为必须槽位

    Map<String, String> getPreconditionDataMap();    //获得上面检验数据的前提条件

}
