package com.cooler.ai.platform.condition;

import com.cooler.ai.platform.entity.ConditionRule;
import com.cooler.ai.platform.model.ConditionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.cooler.ai.platform.condition.BaseCondition.VALICATION_PASSED;

public class BeyondBTCondition extends FatherCondition {

    private static Logger logger = LoggerFactory.getLogger(BeyondBTCondition.class);

    public BeyondBTCondition(String conditionName) {
        super(conditionName);
    }


    @Override
    public boolean isSatisfied(ConditionData conditionData) {
        int validate = validate(conditionData);
        if(validate != VALICATION_PASSED)   return false;

        ConditionRule conditionRule = conditionData.getConditionRule();
        float belief = conditionData.getBelief();
        float beliefThreshold = conditionRule.getBeliefThreshold();
        return belief >= beliefThreshold;
    }

}
