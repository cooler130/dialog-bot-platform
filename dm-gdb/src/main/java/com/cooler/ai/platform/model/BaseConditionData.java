package com.cooler.ai.platform.model;

import com.cooler.ai.platform.entity.ConditionLogic;
import com.cooler.ai.platform.entity.ConditionRule;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class BaseConditionData<V> {

    private ConditionRule conditionRule = null;         //由此conditionRule控制条件检验

    private ConditionLogic conditionLogic = null;       //由此conditionLogic控制条件逻辑

    private Integer paramType = null;                   //变量类型

    private String paramsName = null;                   //变量名称

    private V value = null;                             //数据

    private float belief = 0f;                          //置信度

    private Boolean result = null;                      //此conditionData的检测结果

    private boolean isNecessary = false;                //是否必须

    private Map<String, String> preconditionDataMap = null; //此检测规则的前提条件

}
