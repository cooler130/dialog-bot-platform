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
        String currentState = dialogState.getParamValue(PC.FROM_STATE, Constant.PLATFORM_PARAM);
        if (currentState.equals(Constant.GLOBAL_ERROR) || currentState.equals(Constant.GLOBAL_END)) {
            currentState = Constant.GLOBAL_START;
        }

        //1.通过当前状态currentState找出所有包含intentName的intentSet节点
        List<IntentSetNode> selectedIntentSetNodes = selectIntentSetNodes(domainName, null, currentState, intentName);
        if(selectedIntentSetNodes.size() == 0){
            //todo:当前状态下，遇到了无法处理的意图，无法理解则跳到start节点尝试
        }

        //2.（遍历）收集并检测每一个IntentSet节点，找到第一个"可行"路径
        ConditionPathRecord passedConditionPathRecord = null;
        for (IntentSetNode intentSetNode : selectedIntentSetNodes) {
            passedConditionPathRecord = Neo4jUtil.tryConditionPathsFromIntent(dialogState, currentState, intentName, intentSetNode);
            if(passedConditionPathRecord != null) break;
        }

        //3.获取终止状态
        String endStateName = passedConditionPathRecord.getEndStateName();

    }

    /**
     * 根据当前StateId和intentName来获取符合条件的IntentSet节点
     * @param currentStateName
     * @param intentName
     * @param taskName
     * @param domainName
     * @return
     */
    private static List<IntentSetNode> selectIntentSetNodes(String domainName, String taskName, String currentStateName, String intentName) {
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
