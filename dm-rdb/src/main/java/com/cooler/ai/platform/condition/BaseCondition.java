package com.cooler.ai.platform.condition;

import com.cooler.ai.platform.entity.ConditionLogic;
import com.cooler.ai.platform.entity.ConditionRule;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.model.ConditionContext;
import com.cooler.ai.platform.model.ConditionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.Condition;

import java.util.*;

public class BaseCondition implements Condition<ConditionContext> {
    private static Logger logger = LoggerFactory.getLogger(BaseCondition.class);
    protected String transitionName;
    protected static final int VALICATION_PASSED = 0;

    public BaseCondition(String transitionName) {
        this.transitionName = transitionName;
    }

    public boolean isSatisfied(ConditionContext conditionContext){
        //1.直接获取已经分好组的ConditionDataGroupMap
        Map<Integer, List<ConditionData>> conditionDataGroupMap = conditionContext.getGroupedConditionDatasMap();
        if(conditionDataGroupMap == null || conditionDataGroupMap.size() == 0){                         //这个变迁没有检测条件，则默认通过
            conditionContext.setLastResult(true);
            return true;
        }

        //2.根据conditionLogic中的ruleType，组成一个个校验组（其中：？的由ruleType决定，总体默认为false） lastResult = false || ( true && x1 ? x2 ? x3) || (true && y1 ? y2 ? y3) || (true && z1 ? z2 ? z3)
        boolean lastResult = false;
        List<Integer> groupNums = new ArrayList(conditionDataGroupMap.keySet());
        Collections.sort(groupNums, Comparator.reverseOrder());                                          //必须从小到大依次进行
        for (Integer groupNum : groupNums) {
            List<ConditionData> conditionDataGroup = conditionDataGroupMap.get(groupNum);
            boolean groupResult = true;

            boolean isAllORGroup = true;                                                                                //用来判断这个group是不是全OR条件的group，下面"判断"逻辑（此种情况的group必须至少一个条件为true，整个group才能为true；所有为false，整个group为false。）
            boolean allORGroupResult = false;
            for (int i = 0; i < conditionDataGroup.size(); i ++) {
                ConditionData conditionData = conditionDataGroup.get(i);
                ConditionRule conditionRule = conditionData.getConditionRule();

                Integer ruleType = conditionRule.getRuleType();
                Condition condition = null;
                switch (ruleType) {
                    case Constant.NO_CHECK: {
                        condition = new NoCheckCondition("NoCheckCondition");
                        break;
                    }
                    case Constant.HAS_VALUE: {
                        condition = new HasValueCondition("HasValueCondition");
                        break;
                    }
                    case Constant.BEYOND_THRESHOLD: {
                        condition = new BeyondBTCondition("BeyondBTCondition");
                        break;
                    }
                    case Constant.HIT_RELATION: {
                        condition = new HitRelationCondition("HitRelationCondition");
                        break;
                    }
                    case Constant.HIT_FUNCTION: {
                        condition = new HitFunctionCondition("HitFunctionCondition");
                        break;
                    }
                    case Constant.BEYOND_THRESHOLD_HIT_RELATION: {
                        condition = new BBTHitRelationCondition("BBTHitRelationCondition");
                        break;
                    }
                    case Constant.BEYOND_THRESHOLD_HIT_FUNCTION: {
                        condition = new BBTHitFunctionCondition("BBTHitFunctionCondition");
                        break;
                    }
                }

                boolean isSatisfied = condition.isSatisfied(conditionData);
                conditionData.setResult(isSatisfied);                                                                   //存储第i个检测结果

                ConditionLogic conditionLogic = conditionData.getConditionLogic();
                Integer logicType = conditionLogic.getLogicType();
                if(logicType == Constant.AND || logicType == Constant.NOT){                                 //判断（只要有一个不为OR，就可以说明此group不是全OR条件group）
                    isAllORGroup = false;
                }
                switch (logicType) {
                    case Constant.AND: {
                        groupResult = groupResult && isSatisfied;
                        break;
                    }
                    case Constant.OR: {
                        groupResult = groupResult || isSatisfied;
                        allORGroupResult = allORGroupResult || isSatisfied;
                        break;
                    }
                    case Constant.NOT: {
                        groupResult = groupResult && !isSatisfied;
                        break;
                    }
                }
            }
            if(!isAllORGroup){                                                                                          //大多情况下，不会将一个group放置所有条件都是OR条件，即一般是这个if分支
                lastResult = lastResult || groupResult;
            }else{                                                                                                      //特殊情况下，多个OR条件放到一个group，至少有一个为true，group才为true
                lastResult = lastResult || allORGroupResult;
            }
        }

        conditionContext.setLastResult(lastResult);                                                                     //重新设置最终记录
//        System.out.println("逻辑计算过程 ： " + conditionContext.getAllResultProcess());                                                     //打印计算过程的结果
        return lastResult;

    }

    @Override
    public String name() {
        return transitionName;
    }

}
