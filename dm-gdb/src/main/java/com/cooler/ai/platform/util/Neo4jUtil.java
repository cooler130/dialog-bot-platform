package com.cooler.ai.platform.util;

import com.cooler.ai.platform.facade.model.DialogState;
import com.cooler.ai.platform.model.ConditionNodeRecord;
import com.cooler.ai.platform.model.ConditionPathRecord;
import com.cooler.ai.platform.model.IntentSetNode;
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

import java.util.*;

@Component
@Lazy(false)
public class Neo4jUtil {

	private static Driver neo4jDriver;

	private static Logger log =  LoggerFactory.getLogger(Neo4jUtil.class);

	@Autowired
	@Lazy
	public void setNeo4jDriver(Driver neo4jDriver) {
		Neo4jUtil.neo4jDriver = neo4jDriver;
	}

	/**
	 *
	 * @param cypherSql
	 * @return
	 */
	public static List<Map<String, String>> getIntentSetsFromState(String cypherSql) {
		List<Map<String, String>> ents = new ArrayList<>();
		Session session = null;
		try {
			session = neo4jDriver.session();
			Result result = session.run(cypherSql);
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
			e.printStackTrace();
		} finally {
			session.close();
		}
		return ents;
	}


	/**
	 * 尝试所给IntentSet节点引出的所有条件路径，返回第一个可通过的路径
	 * @param intentSetNode IntentSet节点
	 * @return  可通过的路径
	 */
	public static ConditionPathRecord tryConditionPathsFromIntent(DialogState dialogState, String currentStateName, String intentName, IntentSetNode intentSetNode) {
		String intentSetNodeSUID = intentSetNode.getSUID();

		String directToStatePathFromIntent = "match (is:IntentSet{SUID:" + intentSetNodeSUID + "}), paths=((is)-[]->(s:State)) return paths";
		Path directToStatePath = Neo4jUtil.findDirectToStatePath(directToStatePathFromIntent);
		if(directToStatePath != null) return createConditionPath(currentStateName, intentName, intentSetNode, directToStatePath);

		String conditionsPathFromIntent = "match (is:IntentSet{SUID:" + intentSetNodeSUID + "}), (is)-[]->(c:Condition), paths=((c)-[*..6]->(:State)) where all(x in nodes(paths) where x.class<>'IntentSet') return paths";
		Path firstConditionPath = Neo4jUtil.findFirstConditionPath(conditionsPathFromIntent, dialogState);
		if(firstConditionPath != null) return createConditionPath(currentStateName, intentName, intentSetNode, firstConditionPath);

		return null;
	}

	private static ConditionPathRecord createConditionPath(String currentStateName, String intentName, IntentSetNode intentSetNode, Path conditionPath) {
		List<ConditionNodeRecord> conditionNodeRecords = new ArrayList<>();
		Iterable<Node> nodes = conditionPath.nodes();
		Iterator<Node> iterator = nodes.iterator();
		while(iterator.hasNext()){
			Node nextNode = iterator.next();
			ConditionNodeRecord conditionNodeRecord = new ConditionNodeRecord(
					nextNode.get("SUID").asInt() + "",
					nextNode.get("domain").asString(),
					nextNode.get("name").asString(),
					nextNode.get("class").asString(),
					nextNode.get("type").asString(),
					nextNode.get("param").asString(),
					nextNode.get("option").asString(),
					nextNode.get("value").asString(),
					true
			);
			conditionNodeRecords.add(conditionNodeRecord);
		}
		String endState = conditionPath.end().get("value").asString();			//此路径最后一个节点必须是一个State节点
		ConditionPathRecord conditionPathRecord = new ConditionPathRecord(currentStateName, intentName, intentSetNode.getSUID(), conditionNodeRecords, endState, true);

		return conditionPathRecord;
	}


	public static Path findDirectToStatePath(String directToStatePathFromIntent) {
		Session session = null;
		Result result = null;
		try {
			session = neo4jDriver.session();
			result = session.run(directToStatePathFromIntent);
			if (result != null && result.hasNext()) {
				List<Record> pathRecords = result.list();
				PATH : for (Record pathRecord : pathRecords) {
					List<Pair<String, Value>> pathInfoPairs = pathRecord.fields();
					Pair<String, Value> pathInfoPair = pathInfoPairs.get(0);            //此处断定pathInfoPairs里面只有一个元素，但如果以后发生特殊情况，则还是要遍历。
					Value value = pathInfoPair.value();
					Path path = value.asPath();
					SEGMENT : for (Path.Segment segment : path) {                       //取出当前Path的一个段Segment
						Node end = segment.end();
						if(end.get("value").equals("State")) return path;               //循环出口：如果end节点是一个状态节点，则说明已经走到此Path尾端，则此Path完全可行，返回此Path
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

	public static Path findFirstConditionPath(String cypherSql, DialogState dialogState){
		Session session = null;
		Result result = null;
		try {
			session = neo4jDriver.session();
			result = session.run(cypherSql);
			if (result != null && result.hasNext()) {
				Map<String, Boolean> conditionResMap = new HashMap<>();                 //用来记录一个条件的检测结果，多条path可以公用
				List<Record> pathRecords = result.list();
				PATH : for (Record pathRecord : pathRecords) {
					List<Pair<String, Value>> pathInfoPairs = pathRecord.fields();
					Pair<String, Value> pathInfoPair = pathInfoPairs.get(0);            //此处断定pathInfoPairs里面只有一个元素，但如果以后发生特殊情况，则还是要遍历。
					Value value = pathInfoPair.value();
					Path path = value.asPath();
					SEGMENT : for (Path.Segment segment : path) {                       //取出当前Path的一个段Segment
						Node start = segment.start();
						boolean startConditionRes = checkConditionNode(dialogState, start, conditionResMap);
//                        if(!startConditionRes) continue PATH;

						Relationship relationship = segment.relationship();
						boolean shouldGoOn = checkRelationship(startConditionRes, relationship);
						if(!shouldGoOn) continue PATH;

						Node end = segment.end();
						if(end.get("class").asString().equals("State")) return path;               //循环出口：如果end节点是一个状态节点，则说明已经走到此Path尾端，则此Path完全可行，返回此Path
//                        boolean endConditionRes = checkConditionNode(end, conditionResMap);
//                        if(!endConditionRes) continue PATH;
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

	private static boolean checkConditionNode(DialogState dialogState, Node node, Map<String, Boolean> conditionResMap) {
		String nodeClass = node.get("class").asString();
		String suid = node.get("SUID").asInt() + "";
		if(!nodeClass.equals("Condition") || suid.equals("null")) return false;

		Boolean aBoolean = conditionResMap.get(suid);
		if(aBoolean != null) return aBoolean;

		String domain = node.get("domain").asString();
		String type = node.get("type").asString();
		String param = node.get("param").asString();
		String option = node.get("option").asString();
		String value = node.get("value").asString();
		boolean checkValueRes = checkConditionValue(dialogState, domain, type, param, option, value);
		conditionResMap.put(suid, checkValueRes);

		return checkValueRes;
	}

	private static boolean checkConditionValue(DialogState dialogState, String domain, String paramType, String param, String option, String compareValueStr) {
		if(domain.equals("null") || paramType.equals("null") || param.equals("null") || option.equals("null") || compareValueStr.equals("null")) return false; //todo:这里还是抛出一个专有异常吧
		String currentValueStr = dialogState.getParamValueOrDefaultOfGDB(param, paramType, "none");   //todo：这里来根据 domain + type + param 获取当前业务值
		switch (option){
			case "gt" : {   //greater than  （大于）（比较值必须为数值）
				try{
					float currentValue = Float.parseFloat(currentValueStr);
					float compareValue = Float.parseFloat(compareValueStr);
					return currentValue > compareValue;
				}catch (NumberFormatException e){
					e.printStackTrace();
				}
				break;
			}
			case "ge" : {   //greater equals（大于等于）（比较值必须为数值）
				try{
					float currentValue = Float.parseFloat(currentValueStr);
					float compareValue = Float.parseFloat(compareValueStr);
					return currentValue >= compareValue;
				}catch (NumberFormatException e){
					e.printStackTrace();
				}
				break;
			}
			case "eq" : {   //equals        （等于）（比较值必须为数值）
				try{
					float currentValue = Float.parseFloat(currentValueStr);
					float compareValue = Float.parseFloat(compareValueStr);
					return currentValue == compareValue;
				}catch (NumberFormatException e){
					e.printStackTrace();
				}
				break;
			}
			case "ne" : {   //not equals    （不等于）（比较值必须为数值）
				try{
					float currentValue = Float.parseFloat(currentValueStr);
					float compareValue = Float.parseFloat(compareValueStr);
					return currentValue != compareValue;
				}catch (NumberFormatException e){
					e.printStackTrace();
				}
				break;
			}
			case "lt" : {   //less than     （小于）（比较值必须为数值）
				try{
					float currentValue = Float.parseFloat(currentValueStr);
					float compareValue = Float.parseFloat(compareValueStr);
					return currentValue < compareValue;
				}catch (NumberFormatException e){
					e.printStackTrace();
				}
				break;
			}
			case "le" : {   //less equals   （小于等于）（比较值必须为数值）
				try{
					float currentValue = Float.parseFloat(currentValueStr);
					float compareValue = Float.parseFloat(compareValueStr);
					return currentValue <= compareValue;
				}catch (NumberFormatException e){
					e.printStackTrace();
				}
				break;
			}

			case "bt" : {   //between       （在一个数值范围之中）（边界必须为数值）
				String compareValueStrSub = compareValueStr.substring(1, compareValueStr.length() - 1);         //掐头去尾
				String[] fromToValueStrs = compareValueStrSub.split(",");
				if(fromToValueStrs.length != 2) return false;

				try{
					float fromValue = Float.parseFloat(fromToValueStrs[0].trim());
					float currentValue = Float.parseFloat(currentValueStr);
					float toValue = Float.parseFloat(fromToValueStrs[1].trim());

					boolean leftGE = compareValueStr.startsWith("[");
					boolean leftGT = compareValueStr.startsWith("(");
					boolean rightLT = compareValueStr.endsWith("]");
					boolean rightLE = compareValueStr.endsWith(")");

					boolean leftRes = false;
					boolean rightRes = false;

					if(leftGE) leftRes = currentValue >= fromValue;
					else if(leftGT) leftRes = currentValue > fromValue;

					if(rightLE) rightRes = currentValue <= toValue;
					else if(rightLT) rightRes = currentValue < toValue;

					return leftRes && rightRes;
				}catch (NumberFormatException e){
					e.printStackTrace();
				}
				break;
			}
			case "nb" : {   //not between   （不在一个数值范围之中）（边界必须为数值）
				String compareValueStrSub = compareValueStr.substring(1, compareValueStr.length() - 1);         //掐头去尾
				String[] fromToValueStrs = compareValueStrSub.split(",");
				if(fromToValueStrs.length != 2) return false;

				try{
					float fromValue = Float.parseFloat(fromToValueStrs[0].trim());
					float currentValue = Float.parseFloat(currentValueStr);
					float toValue = Float.parseFloat(fromToValueStrs[1].trim());

					boolean leftGE = compareValueStr.startsWith("[");
					boolean leftGT = compareValueStr.startsWith("(");
					boolean rightLT = compareValueStr.endsWith("]");
					boolean rightLE = compareValueStr.endsWith(")");

					boolean leftRes = false;
					boolean rightRes = false;

					if(leftGE) leftRes = currentValue < fromValue;
					else if(leftGT) leftRes = currentValue <= fromValue;

					if(rightLE) rightRes = currentValue > toValue;
					else if(rightLT) rightRes = currentValue >= toValue;

					return leftRes && rightRes;
				}catch (NumberFormatException e){
					e.printStackTrace();
				}
				break;
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
			case "nn" : {   //not in        （不包含于）（比较值必须为逗号隔开的字符串，各个字符串前后不要空格）
				String[] items = compareValueStr.split(",");
				Set<String> itemSet = new HashSet<>(Arrays.asList(items));
				return !itemSet.contains(currentValueStr);
			}
		}
		return false;
	}

	private static boolean checkRelationship(boolean conditionRes, Relationship relationship) {
		String weather = relationship.get("weather").asString();
		if(weather == null || weather.equals("") || weather.equals("null")) weather = "Y";

		if(conditionRes && weather.equals("Y")) return true;
		else if(conditionRes && weather.equals("N")) return false;
		else if(!conditionRes && weather.equals("Y")) return false;
		else if(!conditionRes && weather.equals("N")) return true;
		return false;
	}

}