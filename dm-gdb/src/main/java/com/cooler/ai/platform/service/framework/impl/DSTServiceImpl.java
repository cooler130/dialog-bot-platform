package com.cooler.ai.platform.service.framework.impl;

import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DialogState;
import com.cooler.ai.platform.model.ConditionPathRecord;
import com.cooler.ai.platform.model.IntentSetNode;
import com.cooler.ai.platform.service.framework.DSTService;
import com.cooler.ai.platform.util.Neo4jUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gdbDstService")
public class DSTServiceImpl implements DSTService {

    private Logger logger = LoggerFactory.getLogger(DSTServiceImpl.class);

    @Override
    public void fsmDSTProcess(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap) {
//        String intentIdStr = dialogState.getParamValue(PC.INTENT_ID, Constant.PLATFORM_PARAM);
//        Integer intentId = Integer.parseInt(intentIdStr);

//        String intentTypeStr = dialogState.getParamValue(PC.INTENT_TYPE, Constant.PLATFORM_PARAM);             //-1，兜底意图；0，系统意图；1，一级语义意图；2，二级语义意图
//        Integer intentType = Integer.parseInt(intentTypeStr);

        String domainName = dialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);
        String taskName = dialogState.getParamValue(PC.TASK_NAME, Constant.PLATFORM_PARAM);
        String intentName = dialogState.getParamValue(PC.INTENT_NAME, Constant.PLATFORM_PARAM);

        String currentState = dialogState.getParamValue(PC.FROM_STATE, Constant.PLATFORM_PARAM);
        if (currentState.equals(Constant.GLOBAL_ERROR) || currentState.equals(Constant.GLOBAL_END)) {
            currentState = Constant.GLOBAL_START;
        }
        String fromState2 = currentState;

        //1.通过当前状态currentState找出所有包含intentName的intentSet节点
        List<IntentSetNode> selectedIntentSetNodes = selectIntentSetNodes(domainName, taskName, currentState, intentName);

        //2.如果找不到任何intentSet，则尝试将intentName转义为标准意图，再尝试采查询。
        String transformIntentName = intentName;
        if(selectedIntentSetNodes.size() == 0){
            String transformIntentNameTmp = transformStandardIntent(domainName, taskName, currentState, intentName);                  //尝试将当前意图转义成标准意图，再进行查询
            if(transformIntentNameTmp != null){
                transformIntentName = transformIntentNameTmp;
                selectedIntentSetNodes = selectIntentSetNodes(domainName, taskName, currentState, transformIntentName);
            }
        }

        //3.如果找不到任何intentSet，则currentState换成start节点尝试。
        if(selectedIntentSetNodes.size() == 0 && currentState != Constant.GLOBAL_START){
            fromState2 = Constant.GLOBAL_START;
            selectedIntentSetNodes = selectIntentSetNodes(domainName, taskName, Constant.GLOBAL_START, intentName);
            if(selectedIntentSetNodes.size() == 0 && !intentName.equals(transformIntentName)){
                selectedIntentSetNodes = selectIntentSetNodes(domainName, taskName, Constant.GLOBAL_START, transformIntentName);
            }
        }

        //4.遍历各个节点
        ConditionPathRecord passedConditionPathRecord = null;
        if(selectedIntentSetNodes.size() == 0){
            //4.1. 没有找到IntentSet节点，则打印日志，看看当前状态没有当前意图是否合理。（最好保存到一个表中）
            logger.error("在状态 {} 下， 意图 {} 没有获取相关的意图集合，进而无法形成条件路径。", currentState, intentName);
        }else{
            //4.2.（遍历）收集并检测每一个IntentSet节点，找到第一个"可行"路径
            for (IntentSetNode intentSetNode : selectedIntentSetNodes) {
                passedConditionPathRecord = Neo4jUtil.tryConditionPathsFromIntent(dialogState, currentState, intentName, intentSetNode);
                if(passedConditionPathRecord != null) break;
            }
        }

        //5.获取终止状态
        String endStateName = (passedConditionPathRecord != null) ? passedConditionPathRecord.getEndStateName() : Constant.GLOBAL_ERROR;
        dialogState.addToParamValueMap(PC.FROM_STATE2, fromState2, Constant.PLATFORM_PARAM);
        dialogState.addToParamValueMap(PC.TRANSFORM_INTENT_NAME, transformIntentName, Constant.PLATFORM_PARAM);
        dialogState.addToParamValueMap(PC.TO_STATE, endStateName, Constant.PLATFORM_PARAM);
    }

    private String transformStandardIntent(String domainName, String taskName, String currentState, String intentName) {
        //todo:后面看看是建立关系型数据库还是直接在neo4j上加一些实体来装载转义意图。
        return null;
    }

    /**
     * 根据当前State和intentName来获取符合条件的IntentSet节点
     * @param currentStateName
     * @param intentName
     * @param taskName
     * @param domainName
     * @return
     */
    private static List<IntentSetNode> selectIntentSetNodes(String domainName, String taskName, String currentStateName, String intentName) {
        //todo:此方法里面暂时没用taskName，如果用了则在下面sql语句中填上State的属性即可，没用是因为neo4j还没有为各个数据加上taskName属性，有待添加

        //查找当前状态出发，能关联上的所有意图集合
        //这里尝试State要有domainName来限制，而intentSet不用domainName来限制，因为可以从一个状态的State接收另一个领域的intent。
        String queryForIntents = "match data=(s1:State{value:'" + currentStateName + "', domain:'" + domainName + "'})-[]->(is:IntentSet{value:'" + intentName + "'}) return is";
        List<Map<String, String>> intentSetNodeDataMaps = Neo4jUtil.getIntentSetsFromState(queryForIntents);
        if(intentSetNodeDataMaps == null || intentSetNodeDataMaps.size() == 0) return new ArrayList<>();

        //可能可以找到多个意图集节点都包含当前意图，则将它们封装到IntentSetNode中，放到一个List里面
        List<IntentSetNode> targetIntentSetNodes = new ArrayList<>();
        for (Map<String, String> nodeDataMap : intentSetNodeDataMaps) {
            if(nodeDataMap.get("class").equals("IntentSet")){
                String intentsTmp = nodeDataMap.get("value");
                String[] intentArray = intentsTmp.split(",");
                Set<String> intents = new HashSet<>(Arrays.asList(intentArray));
                if(intents.contains(intentName)){
                    IntentSetNode intentSetNode = new IntentSetNode(
                            nodeDataMap.get("SUID"),
                            nodeDataMap.get("_cytoscape_network"),
                            nodeDataMap.get("_neo4j_labels"),
                            nodeDataMap.get("name"),
                            nodeDataMap.get("title"),
                            intents
                    );
                    targetIntentSetNodes.add(intentSetNode);
                }
            }
        }
        return targetIntentSetNodes;
    }

}
