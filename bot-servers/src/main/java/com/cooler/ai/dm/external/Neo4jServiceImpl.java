package com.cooler.ai.dm.external;

import com.cooler.ai.dm.entity.Param;
import com.cooler.ai.distribution.facade.constance.Constant;
import com.cooler.ai.distribution.facade.constance.PC;
import com.cooler.ai.distribution.facade.model.DialogState;
import com.cooler.ai.dm.model.ConditionNodeRecord;
import com.cooler.ai.dm.model.ConditionPathRecord;
import com.cooler.ai.dm.model.IntentSetNode;
import com.cooler.ai.dm.service.external.GDBService;
import com.cooler.ai.dm.service.ParamService;
import com.cooler.ai.dm.util.HttpUtil;
import com.cooler.ai.dm.util.ScriptUtil;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@Lazy(false)
public class Neo4jServiceImpl implements GDBService {

	private static Driver neo4jDriver;

	private static Logger logger =  LoggerFactory.getLogger(Neo4jServiceImpl.class);

	@Resource
	private ParamService paramService;

	@Autowired
	@Lazy
	public void setNeo4jDriver(Driver neo4jDriver) {
		Neo4jServiceImpl.neo4jDriver = neo4jDriver;
	}

	/**
	 * 在图数据库中，查询domainName领域，taskName任务，值为stateName的状态下，关联了哪些含有intentName的节点。
	 * @param domainName
	 * @param taskName
	 * @param stateName
	 * @param intentName
	 * @return
	 */
	public List<IntentSetNode> getIntentSetsFromState(String domainName, String taskName, String stateName, String intentName) {
		String queryForIntents = "match data=(s1:State{value:'" + stateName + "', domain:'" + domainName + "', task:'" + taskName + "'})-[]->(is:IntentSet) where '" + intentName + "' in split(is.value, ',') return is";
		List<Map<String, String>> ents = new ArrayList<>();
		Session session = null;
		try {
			session = neo4jDriver.session();
			Result result = session.run(queryForIntents);
			if (result.hasNext()) {
				List<Record> records = result.list();
				for (Record recordItem : records) {
					List<Pair<String, Value>> f = recordItem.fields();
					for (Pair<String, Value> pair : f) {
						Map<String, String> rss = new HashMap<>();
						String typeName = pair.value().type().name();
						if (typeName.equals("NODE")) {
							Node noe4jNode = pair.value().asNode();
							String uuid = String.valueOf(noe4jNode.id());
							Map<String, Object> map = noe4jNode.asMap();
							for (Map.Entry<String, Object> entry : map.entrySet()) {
								String key = entry.getKey();
								rss.put(key, entry.getValue().toString());
							}
							rss.put("uuid", uuid);
							ents.add(rss);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("发生neo4j连接异常：", e);
		} finally {
			session.close();
		}
		if(ents == null || ents.size() == 0) return new ArrayList<>();

		List<IntentSetNode> targetIntentSetNodes = new ArrayList<>();				//可能可以找到多个意图集节点都包含当前意图，则将它们封装到IntentSetNode中，放到一个List里面
		for (Map<String, String> nodeDataMap : ents) {
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

	/**
	 * 检验从IntentSet节点出发的能达到某一个状态的所有路径（带条件节点或无条件节点），返回第一个可检验通过的路径
	 * @param dialogState
	 * @param currentStateName
	 * @param intentName
	 * @param intentSetNode
	 * @return
	 */
	public ConditionPathRecord tryConditionPathsFromIntent(IntentSetNode intentSetNode, DialogState dialogState, String currentStateName, String intentName) {
		String intentSetNodeSUID = intentSetNode.getSUID();

		String directToStatePathFromIntent = "match (is:IntentSet{SUID:" + intentSetNodeSUID + "}), paths=((is)-[]->(s:State)) return paths";
		Path directToStatePath = findDirectToStatePath(directToStatePathFromIntent, intentSetNodeSUID, intentName);
		if(directToStatePath != null) return createConditionPath(currentStateName, intentName, intentSetNode, directToStatePath);

		String conditionsPathFromIntent = "match (is:IntentSet{SUID:" + intentSetNodeSUID + "}), (is)-[]->(c:Condition), paths=((c)-[*..6]->(:State)) where all(x in nodes(paths) where x.class<>'IntentSet') return paths";
		Path firstConditionPath = findFirstConditionPath(conditionsPathFromIntent, intentSetNodeSUID, intentName, dialogState);
		if(firstConditionPath != null) return createConditionPath(currentStateName, intentName, intentSetNode, firstConditionPath);

		return null;
	}

	/**
	 * 根据输入语句，查询直接到一个状态的路径。
	 * @param directToStatePathFromIntent	查询语句 match (is:IntentSet{SUID:"xxx"}), paths=((is)-[]->(s:State)) return paths
	 * @param intentSetNodeSUID
	 * @param intentName
	 * @return 直接到某个状态的路径
	 */
	private Path findDirectToStatePath(String directToStatePathFromIntent, String intentSetNodeSUID, String intentName) {
		Session session = null;
		Result result = null;
		try {
			session = neo4jDriver.session();
			result = session.run(directToStatePathFromIntent);
			if (result != null && result.hasNext()) {
				List<Record> pathRecords = result.list();								//这里理应只有一条路径的，因为一个状态+意图，不结合任何条件，只应该有一条短路径。
				logger.info("DST Pathes count : {} ", pathRecords.size());
				PATH : for (Record pathRecord : pathRecords) {
					showAllPathes(intentSetNodeSUID, intentName, pathRecord);
					List<Pair<String, Value>> pathInfoPairs = pathRecord.fields();
					Pair<String, Value> pathInfoPair = pathInfoPairs.get(0);            //此处断定pathInfoPairs里面只有一个元素，但如果以后发生特殊情况，则还是要遍历。
					Value value = pathInfoPair.value();
					Path path = value.asPath();
					SEGMENT : for (Path.Segment segment : path) {                       //取出当前Path的一个段Segment
						Node end = segment.end();
						String nodeClass = end.get("class").asString();
						if(nodeClass.equals("State")) return path;               //循环出口：如果此段的end节点是一个状态节点（根据查询语句，一定会是状态节点的），则返回此Path
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{
				session.close();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 找到第一条条件完全满足的路径
	 * @param cypherSql	查询的sql语句，形如：match (is:IntentSet{SUID:'xxx'}), (is)-[]->(c:Condition), paths=((c)-[*..6]->(:State)) where all(x in nodes(paths) where x.class<>'IntentSet') return paths
	 * @param intentSetNodeSUID
	 * @param intentName
	 * @param dialogState
	 * @return
	 */
	private Path findFirstConditionPath(String cypherSql, String intentSetNodeSUID, String intentName, DialogState dialogState){
		Session session = null;
		Result result = null;
		try {
			session = neo4jDriver.session();
			result = session.run(cypherSql);
			if (result != null && result.hasNext()) {
				Map<String, Boolean> conditionResCache = new HashMap<>();                 //用来记录一个条件的检测结果缓存
				Map<String, List<Param>> conditionParamsCache = new HashMap<>();					  //用来记录变量的数据缓存
				Map<String, String> paramValueCache = new HashMap<>();
				List<Record> pathRecords = result.list();
				logger.info("DST Pathes count : {} ", pathRecords.size());
				PATH : for (Record pathRecord : pathRecords) {
					showAllPathes(intentSetNodeSUID, intentName, pathRecord);
					List<Pair<String, Value>> pathInfoPairs = pathRecord.fields();
					Pair<String, Value> pathInfoPair = pathInfoPairs.get(0);            //此处断定pathInfoPairs里面只有一个元素，但如果以后发生特殊情况，则还是要遍历。
					Value value = pathInfoPair.value();
					Path path = value.asPath();
					SEGMENT : for (Path.Segment segment : path) {                       //取出当前Path的一个段Segment，每一只检测这一段的"头结点+关联"的整体结果，不检测这一段的尾结点条件。
						Node start = segment.start();
						boolean startConditionRes = checkConditionNode(dialogState, start, conditionResCache, conditionParamsCache, paramValueCache);

						Relationship relationship = segment.relationship();
						boolean shouldGoOn = checkRelationship(startConditionRes, relationship);
						if(!shouldGoOn) continue PATH;

						Node end = segment.end();
						if(end.get("class").asString().equals("State")) return path;    //循环出口：如果end节点是一个状态节点，则说明已经走到此Path尾端，则此Path完全可行，返回此Path
					}
				}
			}
		} catch (Exception e) {
			logger.error("neo4j 路径计算异常：", e);
		} finally {
			try{
				session.close();
			}catch (Exception e){
				logger.error("neo4j 关闭失败：", e);
			}
		}
		return null;
	}

	/**
	 * 打印所有路径的SUID
	 * @param intentSetNodeSUID
	 * @param intentName
	 * @param pathRecord
	 */
	private void showAllPathes(String intentSetNodeSUID, String intentName, Record pathRecord) {
		List<Pair<String, Value>> pathInfoPairs = pathRecord.fields();
		Pair<String, Value> pathInfoPair = pathInfoPairs.get(0);            //此处断定pathInfoPairs里面只有一个元素，但如果以后发生特殊情况，则还是要遍历。
		Value value = pathInfoPair.value();
		Path path = value.asPath();
		Path.Segment currentSegment = null;
		StringBuilder sb = new StringBuilder("( " + intentName + ":" + intentSetNodeSUID + " ) - -> ");
		SEGMENT : for (Path.Segment segment : path) {                       //取出当前Path的一个段Segment，每一只检测这一段的"头结点+关联"的整体结果，不检测这一段的尾结点条件。
			Node start = segment.start();
			String name = start.get("name").asString();
			sb.append(" <").append(name).append(":").append(start.get("SUID").asInt()).append("> ");

			Relationship relationship = segment.relationship();
			String whether = relationship.get("whether").asString();
			boolean noWhether = whether.equals("");
			sb.append(" -").append(noWhether ? " " : whether).append("-> ");

			currentSegment = segment;
		}
		Node lastNode = currentSegment.end();
		sb.append(" [ ")
				.append(lastNode.get("value").asString()).append(":")
				.append(lastNode.get("name").asString()).append(":")
				.append(lastNode.get("SUID").asInt())
				.append(" ] ");
		logger.info("------------------");
		logger.info("Path Attempt     : {}", sb.toString());
	}

	private boolean checkConditionNode(DialogState dialogState, Node node, Map<String, Boolean> conditionResCache, Map<String, List<Param>> conditionParamsCache, Map<String, String> paramValueCache) {
		String nodeClass = node.get("class").asString();
		String suid = node.get("SUID").asInt() + "";
		String name = node.get("name").asString();
		String domain = node.get("domain").asString();
		String task = node.get("task").asString();
		String type = node.get("type").asString();
		String param = node.get("param").asString();
		String option = node.get("option").asString();
		String value = node.get("value").asString();
		String version = dialogState.getParamValue(PC.VERSION, Constant.PLATFORM_PARAM);
		if(!nodeClass.equals("Condition") || suid.equals("null")) {
			logger.info("Condition Error  : 交互图节点参数错误！suid:{} name:{} 的节点理应为Condition节点! 此节点条件不通过！", suid, name);
			return false;
		}

		Boolean checkValueRes = conditionResCache.get(suid);
		if(checkValueRes != null) {
			logger.info("> Condition Check: 条件 {} 验证为 ：{} ，（ 检测是否 {} 类型变量 {} {} {} ?） （使用cache）", name, checkValueRes ? "真" : "假", type, param, option, value);
			return checkValueRes;
		}

		String cacheKey = domain + "_" + task + "_" + param;
		List<Param> conditionParams = conditionParamsCache.get(cacheKey);
		if(conditionParams == null || conditionParams.size() == 0){
			conditionParams = paramService.getConditionParams(domain, task, param, version);
			conditionParamsCache.put(cacheKey, conditionParams);
		}
		Collections.sort(conditionParams);
		checkValueRes = checkConditionValue(dialogState, domain, task, type, param, option, value, conditionParams, paramValueCache);
		logger.info("> Condition Check: 条件 {} 验证为 ：{} ，（ 检测是否 {} 类型变量 {} {} {} ?） ", name, checkValueRes ? "真" : "假", type, param, option, value);
		conditionResCache.put(suid, checkValueRes);

		return checkValueRes;
	}

	private boolean checkConditionValue(DialogState dialogState, String domain, String task, String paramType, String param, String option, String compareValueStr, List<Param> conditionParams, Map<String, String> paramValueCache) {
		if(domain.equals("null") || paramType.equals("null") || param.equals("null") || option.equals("null") || compareValueStr.equals("null") || conditionParams == null) {
			logger.error("Condition Error  : domain:{}, paramType:{}, param:{}, option:{}, compareValueStr:{}, conditionParams:{} 都不可为空！",
					domain, paramType, param, option, compareValueStr, conditionParams);
			return false;
		}

		String currentValueStr = paramValueCache.get(param);
		if(currentValueStr == null || currentValueStr.equals("null")) {
			Map[] fiveMaps = {
					dialogState.getFromModelStateMap(Constant.PLATFORM_PARAM_MAP, Map.class),
					dialogState.getFromModelStateMap(Constant.CUSTOM_PARAM_MAP, Map.class),
					dialogState.getFromModelStateMap(Constant.SLOT_PARAM_MAP, Map.class),
					dialogState.getFromModelStateMap(Constant.BIZ_PARAM_MAP, Map.class),     //第3个Map作为全局变量池（先初始化为DS中的BIZ_PARAM_MAP，后面可对其进行修改）
					new HashMap<String, String>()                                            //第4个Map作为局部变量池
			};
			for (Param conditionParam : conditionParams) {
				Integer acquireType = conditionParam.getAcquireType();
				String acquireContent = conditionParam.getAcquireContent();
				Integer groupNum = conditionParam.getGroupNum();
//			logger.info("Param Getting    : 开始获取变量 {} 的值，获取阶段号：{}，使用 {} 类型的获取方式: {}。", param, groupNum, acquireType == Constant.SCRIPT_ACQUIRE ? "script" : "http", conditionParam.getMsg());
//			logger.info(">> Param Getting : 开始获取变量 {} 的值，获取阶段号：{}，使用 {} 类型的获取方式。", param, groupNum, acquireType == Constant.SCRIPT_ACQUIRE ? "script" : "http");
				if(acquireContent != null && !acquireContent.trim().equals("")){
					if(acquireType == Constant.SCRIPT_ACQUIRE){																//本地脚本计算获取
						Map<String, Map<String, String>> newTwoMaps = ScriptUtil.runScript(acquireContent, fiveMaps[0], fiveMaps[1], fiveMaps[2], fiveMaps[3], fiveMaps[4]);	//带出新的全局变量和局部变量结果值，twoMaps进行更新
						if(newTwoMaps != null){
							fiveMaps[3].putAll(newTwoMaps.get("bps"));
							fiveMaps[4].putAll(newTwoMaps.get("lps"));
						}
					}else if(acquireType == Constant.HTTP_ACQUIRE){															//远程Http调用获取
						Map<String, String> httpParams = HttpUtil.runHttpAction(acquireContent, fiveMaps[0], fiveMaps[1], fiveMaps[2], fiveMaps[3], fiveMaps[4]);
						if(httpParams != null) {
							fiveMaps[4].putAll(httpParams);       //得到的httpParams作为局部变量，如果其有部分变量需要转为全局变量，则加PROCESSED_ACTION的脚本进行变量转移（可能将一个大的对象的属性取出来放到gps和lps中）
						}
					}
				}
			}
			currentValueStr = (String) fiveMaps[4].get("%" + param + "%");							//先从局部变量池中取值，如果没有则从全局变量中取（但通常应该放在局部变量池中）
			logger.info(">> Param Result1 : 参数 {} 的值获取为：{} ，（从 局部变量池(lps) 中获取）。", param, currentValueStr);

			if(currentValueStr == null || currentValueStr.equals("null")) {
				currentValueStr = dialogState.getParamValue(param, paramType);	 											//这一行实际上就相当于从twoMaps[0]中尝试取值
				logger.info(">> Param Result2 : 参数 {} 的值获取为：{} ，（从 DS的4个变量池(pps/sps/cps/bps) 中获取）。", param, currentValueStr);
			}

			if(currentValueStr == null || currentValueStr.equals("null")) {
				logger.error("Param Error     : {} 领域 {} 任务 {} 类型的 {} 变量，没有获得值，请管理员检查是否设置相关脚本或接口！", domain, task, paramType, param);
				return false;
			}
		} else {
			logger.info(">> Param Result0 : 参数 {} 的值获取为：{} ，（从 缓存paramValueCache 中获取） 。", param, currentValueStr);
		}

		paramValueCache.put(param, currentValueStr);											//添加缓存

		try{
			switch (option){
				case "gt" : {   //greater than  （大于）（比较值必须为数值）
					float currentValue = Float.parseFloat(currentValueStr);
					float compareValue = Float.parseFloat(compareValueStr);
					return currentValue > compareValue;
				}
				case "ge" : {   //greater equals（大于等于）（比较值必须为数值）
					float currentValue = Float.parseFloat(currentValueStr);
					float compareValue = Float.parseFloat(compareValueStr);
					return currentValue >= compareValue;
				}
				case "eq" : {   //equals        （等于）（比较值必须为数值）
					float currentValue = Float.parseFloat(currentValueStr);
					float compareValue = Float.parseFloat(compareValueStr);
					return currentValue == compareValue;
				}
				case "ne" : {   //not equals    （不等于）（比较值必须为数值）
					float currentValue = Float.parseFloat(currentValueStr);
					float compareValue = Float.parseFloat(compareValueStr);
					return currentValue != compareValue;
				}
				case "lt" : {   //less than     （小于）（比较值必须为数值）
					float currentValue = Float.parseFloat(currentValueStr);
					float compareValue = Float.parseFloat(compareValueStr);
					return currentValue < compareValue;
				}
				case "le" : {   //less equals   （小于等于）（比较值必须为数值）
					float currentValue = Float.parseFloat(currentValueStr);
					float compareValue = Float.parseFloat(compareValueStr);
					return currentValue <= compareValue;
				}

				case "bt" : {   //between       （在一个数值范围之中）（边界必须为数值）
					String compareValueStrSub = compareValueStr.substring(1, compareValueStr.length() - 1);         //掐头去尾
					String[] fromToValueStrs = compareValueStrSub.split(",");
					if(fromToValueStrs.length != 2) return false;

					float fromValue = Float.parseFloat(fromToValueStrs[0].trim());
					float currentValue = Float.parseFloat(currentValueStr);
					float toValue = Float.parseFloat(fromToValueStrs[1].trim());

					boolean leftGE = compareValueStr.startsWith("[");
					boolean leftGT = compareValueStr.startsWith("(");
					boolean rightLE = compareValueStr.endsWith("]");
					boolean rightLT = compareValueStr.endsWith(")");

					boolean leftRes = false;
					boolean rightRes = false;

					if(leftGE) leftRes = currentValue >= fromValue;
					else if(leftGT) leftRes = currentValue > fromValue;

					if(rightLE) rightRes = currentValue <= toValue;
					else if(rightLT) rightRes = currentValue < toValue;

					return leftRes && rightRes;
				}
				case "nb" : {   //not between   （不在一个数值范围之中）（边界必须为数值）
					String compareValueStrSub = compareValueStr.substring(1, compareValueStr.length() - 1);         //掐头去尾
					String[] fromToValueStrs = compareValueStrSub.split(",");
					if(fromToValueStrs.length != 2) return false;

					float fromValue = Float.parseFloat(fromToValueStrs[0].trim());
					float currentValue = Float.parseFloat(currentValueStr);
					float toValue = Float.parseFloat(fromToValueStrs[1].trim());

					boolean leftGE = compareValueStr.startsWith("[");
					boolean leftGT = compareValueStr.startsWith("(");
					boolean rightLE = compareValueStr.endsWith("]");
					boolean rightLT = compareValueStr.endsWith(")");

					boolean leftRes = false;
					boolean rightRes = false;

					if(leftGE) leftRes = currentValue < fromValue;
					else if(leftGT) leftRes = currentValue <= fromValue;

					if(rightLE) rightRes = currentValue > toValue;
					else if(rightLT) rightRes = currentValue >= toValue;

					return leftRes || rightRes;
				}

				case "is" : {   //is            （是）（比较值必须为字符串）
					return currentValueStr.equals(compareValueStr);
				}
				case "not" : {  //not           （不是）（比较值必须为字符串）
					return !currentValueStr.equals(compareValueStr);
				}

				case "in" : {   //in            （包含于）（比较值必须为逗号隔开的字符串，各个字符串前后不要空格）
					String[] items = compareValueStr.split(",");
					Set<String> itemSet = new HashSet<>(Arrays.asList(items));
					return itemSet.contains(currentValueStr);
				}
				case "ni" : {   //not in        （不包含于）（比较值必须为逗号隔开的字符串，各个字符串前后不要空格）
					String[] items = compareValueStr.split(",");
					Set<String> itemSet = new HashSet<>(Arrays.asList(items));
					return !itemSet.contains(currentValueStr);
				}
			}
		} catch (NumberFormatException e){
			logger.error("获得变量 " + param + " 的值为：" + currentValueStr + "，其转换为Float类型失败！请勿将其与对比值："
					+ compareValueStr + " 进行（ " + option + " ）大小类型比较！", e);
		}

		return false;
	}

	private boolean checkRelationship(boolean conditionRes, Relationship relationship) {
		String weather = relationship.get("whether").asString();
		if(weather == null || weather.equals("") || weather.equals("null")) weather = "Y";

		if(conditionRes && weather.equals("Y")) return true;
		else if(conditionRes && weather.equals("N")) return false;
		else if(!conditionRes && weather.equals("Y")) return false;
		else if(!conditionRes && weather.equals("N")) return true;

		return false;
	}

	/**
	 * 创建选中的路径对象
	 * @param currentStateName
	 * @param intentName
	 * @param intentSetNode
	 * @param conditionPath
	 * @return
	 */
	private ConditionPathRecord createConditionPath(String currentStateName, String intentName, IntentSetNode intentSetNode, Path conditionPath) {
		List<ConditionNodeRecord> conditionNodeRecords = new ArrayList<>();

		Path.Segment segment = null;
		Iterator<Path.Segment> iterator = conditionPath.iterator();
		while(iterator.hasNext()){
			segment = iterator.next();
			Node startNode = segment.start();
			Relationship relationship = segment.relationship();
			ConditionNodeRecord conditionNodeRecord = new ConditionNodeRecord(
					startNode.get("SUID").asInt() + "",
					startNode.get("name").asString(),
					relationship.get("whether").asString()
			);
			conditionNodeRecords.add(conditionNodeRecord);
		}

		String endState = conditionPath.end().get("value").asString();			//此路径最后一个节点必须是一个State节点
		ConditionPathRecord conditionPathRecord = new ConditionPathRecord(currentStateName, intentName, intentSetNode.getSUID(), conditionNodeRecords, endState, true);

		return conditionPathRecord;
	}
}