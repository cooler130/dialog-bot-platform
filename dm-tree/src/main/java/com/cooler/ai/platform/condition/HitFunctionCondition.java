package com.cooler.ai.platform.condition;

import com.cooler.ai.platform.entity.ConditionRule;
import com.cooler.ai.platform.model.ConditionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;

import static com.cooler.ai.platform.condition.BaseCondition.VALICATION_PASSED;

public class HitFunctionCondition extends FatherCondition {

    private static Logger logger = LoggerFactory.getLogger(HitFunctionCondition.class);

    @Resource
    private ConditionFunctionDispatcher conditionFunctionDispatcher = null;

    public HitFunctionCondition(String conditionName) {
        super(conditionName);
    }


    @Override
    public boolean isSatisfied(ConditionData conditionData) {
        int validate = validate(conditionData);
        if(validate != VALICATION_PASSED)   return false;

        ConditionRule conditionRule = conditionData.getConditionRule();
        String checkFunctionCode = conditionRule.getCheckFunctionCode();

        String value = (String)conditionData.getValue();
        ConditionFunction conditionFunction = conditionFunctionDispatcher.getFunction(checkFunctionCode);
        return conditionFunction.isHit(value);
    }

}
