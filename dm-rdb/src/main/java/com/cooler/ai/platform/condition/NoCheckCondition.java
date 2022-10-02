package com.cooler.ai.platform.condition;

import com.cooler.ai.platform.entity.ConditionRule;
import com.cooler.ai.platform.model.ConditionData;
import com.cooler.ai.platform.model.EntityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.cooler.ai.platform.condition.BaseCondition.VALICATION_PASSED;

public class NoCheckCondition extends FatherCondition {

    private static Logger logger = LoggerFactory.getLogger(NoCheckCondition.class);

    public NoCheckCondition(String conditionName) {
        super(conditionName);
    }


    @Override
    public boolean isSatisfied(ConditionData conditionData) {
        int validate = validate(conditionData);
        if(validate != VALICATION_PASSED)   return false;

        ConditionRule conditionRule = conditionData.getConditionRule();
        if(conditionRule == null)   return false;
        Integer conditionRuleId = conditionRule.getId();
        if(conditionRuleId == EntityConstant.PERMISSION_CONDITION_RULE_ID){
            return true;
        }else{
            return false;
        }
    }
}
