package com.cooler.ai.platform.condition;

import com.cooler.ai.platform.entity.ConditionRule;
import com.cooler.ai.platform.model.ConditionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;

import static com.cooler.ai.platform.condition.BaseCondition.VALICATION_PASSED;

public class BBTHitFunctionCondition  extends FatherCondition {

    private static Logger logger = LoggerFactory.getLogger(BBTHitFunctionCondition.class);

    @Resource
    private ConditionFunctionDispatcher conditionFunctionDispatcher = null;

    public BBTHitFunctionCondition(String conditionName) {
        super(conditionName);
    }


    @Override
    public boolean isSatisfied(ConditionData conditionData) {
        int validate = validate(conditionData);
        if(validate != VALICATION_PASSED)   return false;

        ConditionRule conditionRule = conditionData.getConditionRule();
        float beliefThreshold = conditionRule.getBeliefThreshold();
        String checkFunctionCode = conditionRule.getCheckFunctionCode();

        float belief = conditionData.getBelief();
        if(belief < beliefThreshold)    return false;                                                                   //确保先超越置信度阈值
        String value = (String)conditionData.getValue();
        ConditionFunction conditionFunction = conditionFunctionDispatcher.getFunction(checkFunctionCode);
        return conditionFunction.isHit(value);
    }


}
