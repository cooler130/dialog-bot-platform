package com.cooler.ai.platform.condition;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.model.ConditionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.cooler.ai.platform.condition.BaseCondition.VALICATION_PASSED;

public class HasValueCondition extends FatherCondition {

    private static Logger logger = LoggerFactory.getLogger(HasValueCondition.class);

    public HasValueCondition(String conditionName) {
        super(conditionName);
    }


    @Override
    public boolean isSatisfied(ConditionData conditionData) {
        int validate = validate(conditionData);
        if(validate != VALICATION_PASSED)   return false;

        String value = (String)conditionData.getValue();
        if (value != null && !value.equals(Constant.NONE_VALUE)) {
            return true;
        }

        logger.info("HasValueCondition --- conditionData: " + JSON.toJSONString(conditionData) + "，value: " + JSON.toJSONString(value));
        return false;
    }
}
