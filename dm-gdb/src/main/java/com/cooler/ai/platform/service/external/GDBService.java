package com.cooler.ai.platform.service.external;

import com.cooler.ai.platform.facade.model.DialogState;
import com.cooler.ai.platform.model.ConditionPathRecord;
import com.cooler.ai.platform.model.IntentSetNode;
import java.util.*;

public interface GDBService {


	/**
	 * 在图数据库中，查询domainName领域，taskName任务，值为stateName的状态下，关联了哪些含有intentName的节点。
	 * @param domainName
	 * @param taskName
	 * @param stateName
	 * @param intentName
	 * @return
	 */
	List<IntentSetNode> getIntentSetsFromState(String domainName,
													 String taskName,
													 String stateName,
													 String intentName);

	/**
	 * 检验从IntentSet节点出发的能达到某一个状态的所有路径（带条件节点或无条件节点），返回第一个可检验通过的路径
	 * @param dialogState
	 * @param currentStateName
	 * @param intentName
	 * @param intentSetNode
	 * @return
	 */
	ConditionPathRecord tryConditionPathsFromIntent(IntentSetNode intentSetNode,
													DialogState dialogState,
													String currentStateName,
													String intentName);

}