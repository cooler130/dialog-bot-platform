package com.cooler.ai.platform.condition;

import com.cooler.ai.platform.entity.ConditionLogic;
import com.cooler.ai.platform.entity.ConditionRule;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.model.ConditionData;
import com.cooler.ai.platform.model.EntityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.Condition;

import java.util.Arrays;

public class FatherCondition implements Condition<ConditionData> {

    private static Logger logger = LoggerFactory.getLogger(FatherCondition.class);
    private String conditionName = null;

    public FatherCondition(String conditionName) {
        this.conditionName = conditionName;
    }

    public int validate(ConditionData conditionData){
        ConditionRule conditionRule = conditionData.getConditionRule();
        ConditionLogic conditionLogic = conditionData.getConditionLogic();

        String paramsName = conditionRule.getParamName();
        Integer paramsType = conditionRule.getParamType();
        Integer ruleType = conditionRule.getRuleType();
        Integer conditionRuleId = conditionRule.getId();
        Float beliefThreshold = conditionRule.getBeliefThreshold();
        String checkValue = conditionRule.getCheckValue();
        Integer checkRelationType = conditionRule.getCheckRelationType();
        String checkFunctionCode = conditionRule.getCheckFunctionCode();

        boolean isNotTwoGlobalCR = (conditionRuleId != EntityConstant.PERMISSION_CONDITION_RULE_ID && conditionRuleId != EntityConstant.PROHIBITION_CONDITION_RULE_ID);      //非"全允通"或者"全禁通"两个规则
        boolean isRightBeliefThreshold = (beliefThreshold > 0f);                                                                                                             //执行度阈值 > 0
        boolean isValuedCheckValue = (checkValue != null && !checkValue.equals(Constant.NONE_VALUE) && !checkValue.equals(""));                                      //参照值不为空
        boolean isRightCheckRelationType = (checkRelationType != null && checkRelationType >= Constant.R_EQUAL && checkRelationType <= Constant.R_NOT_MATCH_REGEXP); //与参照值的关系类型正确，checkRelationType不为null并且在[1,12]之内
        boolean isValuedCheckFunctionCode = (checkFunctionCode != null && !checkFunctionCode.equals(Constant.NONE_VALUE) && !checkFunctionCode.equals(""));          //参照函数码不为空
        boolean isValuedConditionData = (conditionData != null) && (conditionData.getValue() != null);                                                                      //上下文数据（校验数据）不为空

        if (conditionLogic == null){
            logger.error("规则设置错误！conditionLogic不可为空！");
            return 10;
        }

        //1.槽位参数校验：如果是槽位类型的变量，并且还是必须槽位，那么这个数据体不可为空
        if (paramsType == Constant.SLOT_PARAM && conditionData.isNecessary() && !isValuedConditionData) {
            logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：必须槽位类型的变量 ：" + paramsName + "不可为空值！如果可能为空，请将此槽位设置为非必须。");
            return 101;
        }

        //2.系统参数校验：如果校验的是系统变量，那么设置的变量名paramsName必须是系统变量集合中的一个
        if (paramsType == Constant.PLATFORM_PARAM && !Arrays.asList(PC.PLATFORM_PARAMS).contains(paramsName)) {
            logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：" + paramsName + " 被设置为系统变量，paramsType设置为： " + Constant.PLATFORM_PARAM + "，但其不包含在" + Arrays.toString(PC.PLATFORM_PARAMS));
            return 102;
        }

        //3.定制参数校验：如果校验的是定制变量，那么设置的变量名paramsName必须是定制参数集合中的一个
        if (paramsType == Constant.CUSTOM_PARAM) {
            logger.info("注意！规则号：" + conditionRuleId + " ，" + paramsName + " 被设置为自定义变量，paramsType设置为： " + Constant.CUSTOM_PARAM + "，请确保这个变量在Bot自带的CC2类中有变量名的声明！CC2类是框架中自定义参数的扩展静态变量声明类。");
        }

        //特殊情况的校验：如果此规则的变量名paramsName为"none"或者此变量类型为空变量或者此规则不用检测，则此规则必须为"全允通"或者"全禁通"，其它规则必须要设置准确的paramsName、paramsType
        if ((paramsName.equals(Constant.NONE_VALUE) || paramsType == Constant.NULL_PARAM) && isNotTwoGlobalCR) {
            logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：paramsName为'none'的规则ID必须为1（全允通）或2（全禁通）!");
            return 104;
        }

        switch (ruleType) {
            case Constant.NO_CHECK: {
                if (isNotTwoGlobalCR) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：无需检测的只有两个全局ConditionRule（'全允通'和'全禁通'）!");
                    return 1;
                }
                break;
            }
            case Constant.HAS_VALUE: {                                                                                  //此种情况如果没有值，则并不表示规则设置错误！
//                if (!isValuedConditionData) {
//                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：有值检测不通过，约定必须要有值，至少为\"\"，不能为null!");
//                    return 11;
//                }
                break;
            }
            case Constant.BEYOND_THRESHOLD: {
                if (!isValuedConditionData) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：有值检测不通过，约定必须要有值，至少为\"\"，不能为null!");
                    return 21;
                }
                if (!isRightBeliefThreshold) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：阈值检测不通过，阈值大于0!");
                    return 22;
                }
                break;
            }
            case Constant.HIT_RELATION: {
                if (!isValuedConditionData) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：有值检测不通过，约定必须要有值，至少为\"\"，不能为null!");
                    return 31;
                }
                if (!isValuedCheckValue) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：有值检测不通过，满足某关系，那么对比值要有值!");
                    return 32;
                }
                if (!isRightCheckRelationType) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：关系类型检测不通过，满足某关系，那么关系类型在[1,12]!");
                    return 33;
                }
                break;
            }
            case Constant.HIT_FUNCTION: {
                if (!isValuedConditionData) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：有值检测不通过，约定必须要有值，至少为\"\"，不能为null!");
                    return 41;
                }
                if (!isValuedCheckFunctionCode) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：关系函数编码检测不通过，满足某函数，那么函数Code不为空!");
                    return 42;
                }
                break;
            }
            case Constant.BEYOND_THRESHOLD_HIT_RELATION: {
                if (!isValuedConditionData) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：有值检测不通过，约定必须要有值，至少为\"\"，不能为null!");
                    return 51;
                }
                if (!isRightBeliefThreshold) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：阈值检测不通过，阈值大于0!");
                    return 52;
                }
                if (!isValuedCheckValue) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：有值检测不通过，满足某关系，那么对比值要有值!");
                    return 53;
                }
                if (!isRightCheckRelationType) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：关系类型检测不通过，满足某关系，那么关系类型在[1,12]!");
                    return 54;
                }
                break;
            }
            case Constant.BEYOND_THRESHOLD_HIT_FUNCTION: {
                if (!isValuedConditionData) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：有值检测不通过，约定必须要有值，至少为\"\"，不能为null!");
                    return 61;
                }
                if (!isRightBeliefThreshold) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：阈值检测不通过，阈值大于0!");
                    return 62;
                }
                if (!isValuedCheckFunctionCode) {
                    logger.error("规则设置错误！规则号：" + conditionRuleId + " ，原因：关系函数编码检测不通过，满足某函数，那么函数Code不为空!");
                    return 63;
                }
                break;
            }
        }
        return BaseCondition.VALICATION_PASSED;
    }


    @Override
    public boolean isSatisfied(ConditionData conditionData) {
        return false;
    }

    @Override
    public String name() {
        return conditionName;
    }
}
