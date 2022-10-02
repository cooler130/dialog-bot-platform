package com.cooler.ai.platform.service.framework.impl;

import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DialogState;
import com.cooler.ai.platform.model.ConditionNode;
import com.cooler.ai.platform.model.ConditionPath;
import com.cooler.ai.platform.model.IntentSetNode;
import com.cooler.ai.platform.service.framework.DSTService;
import com.cooler.ai.platform.util.Neo4jUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("dstService")
public class DSTServiceImpl implements DSTService {

    private Logger logger = LoggerFactory.getLogger(DSTServiceImpl.class);

    @Override
    public void fsmDSTProcess(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap) {
        //todo:使用neo4j来解析流程。

        String intentIdStr = dialogState.getParamValue(PC.INTENT_ID, Constant.PLATFORM_PARAM);
        Integer intentId = Integer.parseInt(intentIdStr);

        String intentName = dialogState.getParamValue(PC.INTENT_NAME, Constant.PLATFORM_PARAM);

        String intentTypeStr = dialogState.getParamValue(PC.INTENT_TYPE, Constant.PLATFORM_PARAM);                         //-1，兜底意图；0，系统意图；1，一级语义意图；2，二级语义意图
        Integer intentType = Integer.parseInt(intentTypeStr);

        String domainName = dialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);

        String taskName = dialogState.getParamValue(PC.TASK_NAME, Constant.PLATFORM_PARAM);

        String fromStateIdStr = dialogState.getParamValue(PC.FROM_STATE_ID, Constant.PLATFORM_PARAM);
        Integer currentStateId = Integer.parseInt(fromStateIdStr);
        if (currentStateId == Constant.GLOBAL_ERROR_ID || currentStateId == Constant.GLOBAL_END_ID) {
            currentStateId = Constant.GLOBAL_START_ID;
        }

        //1.通过当前状态currentStateId找出所有包含intentName的intentSet节点
        List<IntentSetNode> selectedIntentSetNodes = selectIntentSetNodes(currentStateId, intentName, taskName, domainName);
        if(selectedIntentSetNodes == null || selectedIntentSetNodes.size() == 0){
            //todo:当前状态下，遇到了无法处理的意图
        }

        //2.（遍历）收集并检测每一个IntentSet节点，找到第一个"可行"路径
        ConditionPath conditionPath = null;
        for (IntentSetNode selectedIntentSetNode : selectedIntentSetNodes) {
            conditionPath = dfsIntentSetNode(selectedIntentSetNode);
            if(conditionPath != null) break;
        }

        String endStateName = conditionPath.getEndStateName();

    }

    //todo:可以写测试方法，对下面的函数进行测试。。。。。。。。。。。。。。。。。。。。。。。。。。。。

    /**
     * 根据当前StateId和intentName来获取符合条件的IntentSet节点
     * @param currentStateId
     * @param intentName
     * @param taskName
     * @param domainName
     * @return
     */
    private List<IntentSetNode> selectIntentSetNodes(Integer currentStateId, String intentName, String taskName, String domainName) {
        //查找当前状态出发，能关联上的所有意图集合
        String queryForIntents = "match (s:State{title:'" + currentStateId + "'})-[t:Transation]->(is:IntentSet) return is";
        List<Map<String, String>> intentSetNodeDataMaps = Neo4jUtil.getNodeDataMaps(queryForIntents);
        if(intentSetNodeDataMaps == null || intentSetNodeDataMaps.size() == 0) return new ArrayList<>();

        //可能可以找到多个意图集节点都包含当前意图，则将它们封装到IntentSetNode中，放到一个List里面
        List<IntentSetNode> targetIntentSetNodes = new ArrayList<>();
        for (Map<String, String> nodeDataMap : intentSetNodeDataMaps) {
            if(nodeDataMap.get("_neo4j_label").equals("[\"IntentSet\"]")){
                Set<String> intents = new HashSet<>(Arrays.asList(nodeDataMap.get("intents").split(",")));
                if(intents.contains(intentName)){
                    IntentSetNode intentSetNode = new IntentSetNode(
                            nodeDataMap.get("SUID"),
                            nodeDataMap.get("_cytoscape_network"),
                            nodeDataMap.get("_neo4j_label"),
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

    /**
     * 得到传入的intentSet节点的所有首ConditionNode集合，然后深度遍历这些ConditionNode
     * @param intentSetNode
     * @return  一个可执行路径
     */
    private static ConditionPath dfsIntentSetNode(IntentSetNode intentSetNode) {
        String intentSetSUID = intentSetNode.getSUID();
        String intentName = intentSetNode.getName();

        ConditionPath targetConditionPath = null;
        List<ConditionNode> topConditionNodes = getAllChildren(intentSetSUID, true);                      //得到intentSet节点的所有头部Condition节点
        for (ConditionNode topConditionNode : topConditionNodes) {
            List<List<ConditionNode>> conditionNodesList = dfsConditionNode(topConditionNode);                          //对每一个头部节点开始深度优先遍历，得到具体的ConditionNode链
            targetConditionPath = checkConditionPaths(intentSetSUID, intentName, conditionNodesList);                   //检测这条ConditionNode链，如果所有条件通过，则包装成conditionPath返回出来
            if(targetConditionPath != null) break;                                                                      //检测返回出来的conditionPath是否为空，不为空则算找到第一个目标ConditionPath返回出来
        }
        return targetConditionPath;
    }

    /**
     * 深度遍历输入的首ConditionNode
     * @param topConditionNode
     * @return
     */
    private static List<List<ConditionNode>> dfsConditionNode(ConditionNode topConditionNode) {
        Stack<ConditionNode> conditionNodeStack = new Stack<>();
        conditionNodeStack.push(topConditionNode);

        List<ConditionNode> conditionNodes = new ArrayList<>();
        conditionNodes.add(topConditionNode);

        Stack<List<ConditionNode>> pathStack = new Stack<>();
        pathStack.push(conditionNodes);

        List<List<ConditionNode>> result = new ArrayList<>();
        while(!conditionNodeStack.isEmpty()){
            ConditionNode curConditionNode = conditionNodeStack.pop();
            List<ConditionNode> allChild = getAllChildren(curConditionNode.getSUID(), false);
            List<ConditionNode> curPath = pathStack.pop();
            if(allChild == null || allChild.size() <= 0){
                result.add(curPath);
            }else{
                int childSize = allChild.size();
                for(int i = childSize - 1; i >= 0; i --){
                    ConditionNode conditionNodeTmp = allChild.get(i);
                    conditionNodeStack.push(conditionNodeTmp);
                    List<ConditionNode> conditionNodesTmp = new ArrayList<>(curPath);
                    conditionNodesTmp.add(conditionNodeTmp);
                    pathStack.push(conditionNodesTmp);
                }
            }
        }
        return result;
    }


    /**
     * 通过节点SUID，获得IntentSet节点或Condition节点的下一层所有Condition节点
     * @param nodeSUID
     * @param isIntentSetNode
     * @return
     */
    private static List<ConditionNode> getAllChildren(String nodeSUID, boolean isIntentSetNode){
        List<ConditionNode> targetConditionNodes = new ArrayList<>();
        String queryForConditions = null;
        if(isIntentSetNode){
            queryForConditions = "match (is:IntentSet{SUID:'" + nodeSUID + "'})-[]->(c:Condition) return c";
        }else{
            queryForConditions = "match (c1:Condition{SUID:'" + nodeSUID + "'})-[]->(c:Condition) return c";
        }
        List<Map<String, String>> conditionNodeDataMaps = Neo4jUtil.getNodeDataMaps(queryForConditions);
        if(conditionNodeDataMaps == null || conditionNodeDataMaps.size() == 0) return targetConditionNodes;

        for (Map<String, String> conditionNodeDataMap : conditionNodeDataMaps) {
            if(conditionNodeDataMap.get("_neo4j_label").equals("[\"Condition\"]")){
                ConditionNode conditionNode = new ConditionNode(
                        conditionNodeDataMap.get("SUID"),
                        conditionNodeDataMap.get("_cytoscape_network"),
                        conditionNodeDataMap.get("_neo4j_label"),
                        conditionNodeDataMap.get("name"),
                        conditionNodeDataMap.get("title"),

                        conditionNodeDataMap.get("type"),
                        conditionNodeDataMap.get("param"),
                        conditionNodeDataMap.get("option"),
                        conditionNodeDataMap.get("value"),

                        null
                );
                targetConditionNodes.add(conditionNode);
            }
            if(conditionNodeDataMap.get("_neo4j_label").equals("[\"State\"]")){
                ConditionNode stateNode = new ConditionNode(
                        conditionNodeDataMap.get("SUID"),
                        conditionNodeDataMap.get("_cytoscape_network"),
                        conditionNodeDataMap.get("_neo4j_label"),
                        conditionNodeDataMap.get("name"),
                        conditionNodeDataMap.get("title"),

                        null,
                        null,
                        null,
                        null,

                        null
                );
                targetConditionNodes.add(stateNode);                    //把这个State节点当做Condition节点装进去。
            }
        }

        return targetConditionNodes;
    }

    /**
     * 检测所有的ConditionPath，检测全通过则返回出来
     * @param intentSetSUID
     * @param intentName
     * @param conditionNodesList
     * @return
     */
    private static ConditionPath checkConditionPaths(String intentSetSUID, String intentName, List<List<ConditionNode>> conditionNodesList) {
        //todo:验证
        for (List<ConditionNode> conditionNodes : conditionNodesList) {
            ConditionNode lastConditionNode = conditionNodes.get(conditionNodes.size() - 1);                            //最后一个ConditionNode用来记录State节点信息
            String stateSUID = lastConditionNode.getSUID();
            String stateName = lastConditionNode.getName();
            ConditionPath conditionPath = new ConditionPath(intentSetSUID, intentName, conditionNodes, stateSUID, stateName, null);
            checkConditionPath(conditionPath);
            if(conditionPath.getAllPassed()) return conditionPath;
        }
        return null;
    }

    /**
     * 检测单一的conditionPath，检测通过则赋值allPassed
     * @param conditionPath
     */
    private static void checkConditionPath(ConditionPath conditionPath) {

    }

//    public void fsmDSTProcess2(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap) {
//        //todo:使用neo4j来解析流程。
//
//        String intentIdStr = dialogState.getParamValue(PC.INTENT_ID, Constant.PLATFORM_PARAM);
//        Integer intentId = Integer.parseInt(intentIdStr);
//
//        String intentName = dialogState.getParamValue(PC.INTENT_NAME, Constant.PLATFORM_PARAM);
//
//        String intentTypeStr = dialogState.getParamValue(PC.INTENT_TYPE, Constant.PLATFORM_PARAM);                         //-1，兜底意图；0，系统意图；1，一级语义意图；2，二级语义意图
//        Integer intentType = Integer.parseInt(intentTypeStr);
//
//        String domainName = dialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);
//
//        String taskName = dialogState.getParamValue(PC.TASK_NAME, Constant.PLATFORM_PARAM);
//
//        String fromStateIdStr = dialogState.getParamValue(PC.FROM_STATE_ID, Constant.PLATFORM_PARAM);
//        Integer currentStateId = Integer.parseInt(fromStateIdStr);
//        if (currentStateId == Constant.GLOBAL_ERROR_ID || currentStateId == Constant.GLOBAL_END_ID) {
//            currentStateId = Constant.GLOBAL_START_ID;
//        }
//
//        //查找当前状态出发，能关联上的所有意图集合
//        String queryForIntents = "match (s:State{title:'" + currentStateId + "'})-[t:Transation]->(is:IntentSet) return is";
//        List<Map<String, String>> intentSetNodeDataMaps = Neo4jUtil.getNodeDataMaps(queryForIntents);
//
//        //可能可以找到多个意图集节点都包含当前意图，则将它们封装到IntentSetNode中，放到一个List里面
//        List<IntentSetNode> targetIntentSetNodes = new ArrayList<>();
//        for (Map<String, String> nodeDataMap : intentSetNodeDataMaps) {
//            if(nodeDataMap.get("_neo4j_label").equals("[\"IntentSet\"]")){
//                Set<String> intents = new HashSet<>(Arrays.asList(nodeDataMap.get("intents").split(",")));
//                if(intents.contains(intentName)){
//                    IntentSetNode intentSetNode = new IntentSetNode(
//                            nodeDataMap.get("SUID"),
//                            nodeDataMap.get("_cytoscape_network"),
//                            nodeDataMap.get("_neo4j_label"),
//                            nodeDataMap.get("name"),
//                            nodeDataMap.get("title"),
//                            intents
//                    );
//                    targetIntentSetNodes.add(intentSetNode);
//                }
//            }
//        }
//
//
//        List<Map<String, String>> conditionNodeDataMaps = null;
//        Map<String, IntentSetNode> intentSetNodeMap = new HashMap<>();
//        Map<String, List<ConditionNode>> intentConditionMap = new HashMap<>();
//        for (IntentSetNode targetIntentSetNode : targetIntentSetNodes) {
//            String intentSetSuid = targetIntentSetNode.getSUID();
//            intentSetNodeMap.put(intentSetSuid, targetIntentSetNode);
//            //查找intentSetSuid出发，能关联上多少Condition节点
//            String queryForConditions = "match (is:IntentSet{SUID:'" + intentSetSuid + "'})-[]->(c:Condition) return c";
//            conditionNodeDataMaps = Neo4jUtil.getNodeDataMaps(queryForConditions);
//
//            List<ConditionNode> targetConditionNodes = new ArrayList<>();
//
//            for (Map<String, String> conditionNodeDataMap : conditionNodeDataMaps) {
//                if(conditionNodeDataMap.get("_neo4j_label").equals("[\"Condition\"]")){
//                    ConditionNode conditionNode = new ConditionNode(
//                            conditionNodeDataMap.get("SUID"),
//                            conditionNodeDataMap.get("_cytoscape_network"),
//                            conditionNodeDataMap.get("_neo4j_label"),
//                            conditionNodeDataMap.get("name"),
//                            conditionNodeDataMap.get("title"),
//
//                            conditionNodeDataMap.get("type"),
//                            conditionNodeDataMap.get("param"),
//                            conditionNodeDataMap.get("option"),
//                            conditionNodeDataMap.get("value"),
//                            null
//                    );
//                    targetConditionNodes.add(conditionNode);
//                }
//            }
//            intentConditionMap.put(intentSetSuid, targetConditionNodes);
//            //todo:将这个IntentSet的条件节点集合进行检测，返回检测结果
//
//        }
//        intentConditionMap = checkCondition(intentConditionMap);
//        IntentSetNode targetIntentSetNode = null;
//        ConditionNode targetConditionNode = null;
//        boolean allPassed = false;
//        for (String intentSetNodeId : intentConditionMap.keySet()) {
//            List<ConditionNode> conditionNodes = intentConditionMap.get(intentSetNodeId);
//            for (ConditionNode conditionNode : conditionNodes) {
//                if(conditionNode.getPassed()){                                          //找到第一个通过的就立刻跳出
//                    targetIntentSetNode = intentSetNodeMap.get(intentSetNodeId);
//                    targetConditionNode = conditionNode;
//                    break;
//                }
//            }
//        }
//        //todo：从targetConditionNode里面找后续条件节点，进行验证
//
//        for (String classifiedKey : classifiedResultMap.keySet()) {
//            dialogState.addToParamValueMapDirectly(classifiedKey, classifiedResultMap.get(classifiedKey));              //已经分好类的key直接加入
//        }
//        dialogState.addToParamValueMap(PC.TO_STATE_ID, currentStateId + "", Constant.PLATFORM_PARAM);         //resultMap中的currentStateId可能只是阶段性的数据，不准确，所以要重设
//        dialogState.addToParamValueMap(PC.TRANSFORM_INTENT_NAME, transformIntentName, Constant.PLATFORM_PARAM);
//
//
//
//    }
//
//    private Map<String, List<ConditionNode>> checkCondition(Map<String, List<ConditionNode>> intentConditionMap) {
//        return null;
//    }

}
