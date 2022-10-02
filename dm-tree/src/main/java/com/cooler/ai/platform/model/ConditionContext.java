package com.cooler.ai.platform.model;

import com.cooler.ai.platform.entity.ConditionLogic;
import com.cooler.ai.platform.entity.ConditionRule;
import com.cooler.ai.platform.facade.constance.Constant;
import lombok.Data;
import java.util.*;

@Data
public class ConditionContext<T> {

    private String name = null;
    private Set<String> preprocessParamNames = new HashSet<>();
    private List<ConditionData> conditionDatas = new ArrayList<>();
    private Map<Integer, List<ConditionData>> groupedConditionDatasMap = null;
    private Boolean lastResult = false;

    public void addConditionData(ConditionData conditionData){
        conditionDatas.add(conditionData);
    }

    public ConditionContext(String name) {
        this.name = name;
    }

    public boolean hadBePreprocessed(String paramName){
        return preprocessParamNames.contains(paramName);
    }

    public void recordPreprocessedParamName(String paramName){
        preprocessParamNames.add(paramName);
    }

    public Map<Integer, List<ConditionData>> getGroupedConditionDatasMap(){
        if(groupedConditionDatasMap != null) return groupedConditionDatasMap;
        Map<Integer, List<ConditionData>> conditionDataGroupMap = new HashMap<>();
        for (ConditionData conditionData : conditionDatas) {
            ConditionLogic conditionLogic = conditionData.getConditionLogic();
            Integer groupNum = conditionLogic.getGroupNum();
            List<ConditionData> conditionDataGroup = conditionDataGroupMap.get(groupNum);
            if(conditionDataGroup == null){
                conditionDataGroup = new ArrayList<>();
            }
            conditionDataGroup.add(conditionData);
            conditionDataGroupMap.put(groupNum, conditionDataGroup);
        }
        groupedConditionDatasMap = conditionDataGroupMap;
        return groupedConditionDatasMap;
    }

    /**
     * 获取FIRST_BAD_CONDITION_DATA，即第一个导致变迁失败的变迁条件
     * @return
     */
    public ConditionData findFirstBadConditionData() {
        if (groupedConditionDatasMap != null) {
            List<Integer> groupNums = new ArrayList<>(groupedConditionDatasMap.keySet());
            Collections.sort(groupNums, Collections.reverseOrder());                        //从小到大排列，并依次遍历
            List<ConditionData> noNeedNopassNoticeCDs = new ArrayList<>();
            for (Integer groupNum : groupNums) {
                List<ConditionData> conditionDatas = groupedConditionDatasMap.get(groupNum);
                if(conditionDatas != null && conditionDatas.size() > 0){
                    for (ConditionData conditionData : conditionDatas) {
                        if(!conditionData.getResult()){                                          //如果此ContextData的result == false
                            ConditionLogic conditionLogic = conditionData.getConditionLogic();
                            Integer logicType = conditionLogic.getLogicType();
                            Byte nopassNotice = conditionLogic.getNopassNotice();
                            if(logicType == Constant.AND || logicType == Constant.NOT){
                                if(nopassNotice == (byte)1){
                                    return conditionData;                   //优先返回的ConditionData，它的conditionLogic是并、非（它是必须通过的）,并且它是需要提示/被询问的
                                }else{
                                    noNeedNopassNoticeCDs.add(conditionData);//如果需要提示/被询问的没有，则收集不需要提示/被询问的，作为后补，后面返回第一个
                                }
                            }
                        }
                    }
                }
            }
            if(noNeedNopassNoticeCDs.size() > 0){
                return noNeedNopassNoticeCDs.get(0);
            }
        }
        return null;
    }

    /**
     * 获取推导出结果的所有检测过程记录
     * 解释：下面4种情况，前3种有逻辑不变性，而第4中没有
     * true && (true) -> true
     * true || (true) -> true
     * true && (false) -> false
     * true || (false) -> true (此第四种情况不符合不变性，所以需要改变，那么就要检测，是不是一个group里面全部为OR条件，如果有，则用"false"开头，因为见下面两个逻辑式，下面两个才保持了这种不变性)
     *
     * false || (true) -> true
     * false || (false) -> false
     * @return
     */
    public String getAllResultProcess(){
        if(groupedConditionDatasMap != null){                                               //此Map不为空，则代表此ConditionContext被计算过了，才会有AllResultProcess结果，不然返回空
            List<Integer> groupNums = new ArrayList<>(groupedConditionDatasMap.keySet());
            Collections.sort(groupNums, Collections.reverseOrder());                        //从小到大排列，并依次遍历
            StringBuffer sbAll = new StringBuffer("\nFALSE || ").append("\n");

            for (Integer groupNum : groupNums) {
                List<ConditionData> conditionDatas = groupedConditionDatasMap.get(groupNum);

                StringBuffer sbNotAllORGroup = new StringBuffer("\t" + groupNum + ":( True \n\t\t");
                StringBuffer sbAllORGroup = new StringBuffer("\t" + groupNum + ":全或组( False \n\t\t");
                boolean isAllORGroup = true;

                for (ConditionData conditionData : conditionDatas) {
                    ConditionRule conditionRule = conditionData.getConditionRule();
                    ConditionLogic conditionLogic = conditionData.getConditionLogic();
                    Object value = conditionData.getValue();

                    Integer conditionRuleId = conditionRule.getId();
                    String paramsName = conditionRule.getParamName();
                    Integer checkRelationType = conditionRule.getCheckRelationType();
                    String ruleExpression = Constant.RULE_EXPRESSION_MAP.get(checkRelationType);
                    String checkValue = conditionRule.getCheckValue();
                    Integer conditionLogicId = conditionLogic.getId();
                    Integer transitionId = conditionLogic.getTransitionId();
                    Integer logicType = conditionLogic.getLogicType();
                    if(logicType == Constant.AND || logicType == Constant.NOT){
                        isAllORGroup = false;
                    }
                    String logicTypeStr = null;
                    switch (logicType){
                        case Constant.AND : {
                            logicTypeStr = " && ";
                            break;
                        }
                        case Constant.OR : {
                            logicTypeStr = " || ";
                            break;
                        }
                        case Constant.NOT : {
                            logicTypeStr = " && ! ";
                            break;
                        }
                    }

                    boolean conditionDataResult = conditionData.getResult();
                    String resultSection = "(T:" + transitionId + "_CL:" + conditionLogicId + "_CR:" + conditionRuleId + " | 检测参数：" + paramsName + " 检验: [" + value + " 是否 " + ruleExpression + " " + checkValue + "] -> 结果：" + conditionDataResult + ")";
                    sbNotAllORGroup.append(logicTypeStr).append(resultSection).append("\n\t\t");
                    sbAllORGroup.append(logicTypeStr).append(resultSection).append("\n\t\t");
                }
                sbNotAllORGroup.append(" )").append("\n");
                sbAllORGroup.append(" )").append("\n");

                String groupResult = isAllORGroup ? sbAllORGroup.toString() : sbNotAllORGroup.toString();
                sbAll.append(groupResult);
            }

            sbAll.append(" --> ").append(lastResult);
            return sbAll.toString();
        }
        return null;
    }
}
