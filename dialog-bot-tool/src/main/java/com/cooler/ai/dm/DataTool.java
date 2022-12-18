package com.cooler.ai.dm;

import com.cooler.ai.dm.entity.Param;
import com.cooler.ai.dm.entity.Policy;
import com.cooler.ai.dm.entity.PolicyAction;
import com.cooler.ai.dm.entity.PolicyCondition;
import com.cooler.ai.dm.model.IntentSetNode;
import com.cooler.ai.dm.model.StateNode;
import com.cooler.ai.dm.service.ParamService;
import com.cooler.ai.dm.service.PolicyActionService;
import com.cooler.ai.dm.service.PolicyConditionService;
import com.cooler.ai.dm.service.PolicyService;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.util.Pair;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

/**
 * 作用：此工具用来生成Policy、PolicyCondition、PolicyAction、Param 这4张表的基本数据。
 * 步骤：
 *      1.先建好一个bot所需的5张表结构， param、policy、policy_condition、policy_action、transform_relation，
 *          使用脚本dm_tables.sql，注意先自建一个数据库，打开dm_tables.sql，修改数据库名称；
 *          此项目中的 resources/properties/jdbc.properties 中的数据库名称和账号密码也要提前修改；
 *
 *      2.确保交互流程图设计正确，数据准确；
 *
 *      3.执行此脚本，检查上面4个表中生成的数据，policy、policy_condition 表中数据生成完毕；
 *
 *      4.自己编写param、policy_action中的HTTP或SCRIPT的调用内容或者话术模板内容
 *          （param表acquire_content字段和policy_action的action_content字段）；
 *
 *      5.根据场景需要，在transform_relation表中建立意图转义关联关系记录。
 */

public class DataTool {

    public static final Driver neo4jDriver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "zs86529374" ) );

    public static void main(String[] args) {

        BeanFactory beanFactory = new ClassPathXmlApplicationContext("applicationContext.xml");
        PolicyService policyService = (PolicyService) beanFactory.getBean("policyService");
        PolicyConditionService policyConditionService = (PolicyConditionService) beanFactory.getBean("policyConditionService");
        PolicyActionService policyActionService = (PolicyActionService) beanFactory.getBean("policyActionService");
        ParamService paramService = (ParamService) beanFactory.getBean("paramService");

        //0.设置好默认策略和默认动作
        String domainName = "burouter2";
        String taskName = "bu_route2";

        policyService.insert(new Policy(1,"default_policy", domainName, taskName, "global_any", "no_intent", "global_end", (byte)1, "默认兜底策略"));
        policyActionService.insert(new PolicyAction(1, "default_action", 1, 2, 1, "抱歉！没有理解您的意思，请换个说法再说一次吧！", 1, "全局兜底动作"));

        //1.根据domain、task查询所有的状态，开始遍历这些状态；
        List<StateNode> stateNodes = DataTool.selectAllStatesFromDomainTask(domainName, taskName);
        Map<String, Param> paramsMap = new HashMap<>();

        //2.查询一个状态出发的所有意图；
        for (StateNode stateNode : stateNodes) {
            String fromState = stateNode.getValue();
            List<IntentSetNode> intentSetNodes = DataTool.selectAllIntentSetFromDTS(domainName, taskName, fromState);

            for (IntentSetNode intentSetNode : intentSetNodes) {
                String suid = intentSetNode.getSUID();
                String intentNames = intentSetNode.getValue();

                //3.查询一个状态下所有意图出发到所有状态结尾的路径，两种情况：不含条件和含条件。（得到fromState、intentNames、toState，记录路径下所有Conditions）
                String directToStatePathFromIntent = "match (is:IntentSet{SUID:" + suid + "}), paths = ((is)-[]->(s:State)) return paths";
                List<Path> directToStatePath = DataTool.findDirectToStatePath(directToStatePathFromIntent);

                String conditionsPathFromIntent = "match (is:IntentSet{SUID:" + suid + "}), (is)-[]->(c:Condition), paths=((c)-[*..6]->(:State)) where all(x in nodes(paths) where x.class<>'IntentSet') return paths";
                List<Path> firstConditionPath = DataTool.findDirectToStatePath(conditionsPathFromIntent);

                List<Path> paths = new ArrayList<>();
                paths.addAll(directToStatePath);
                paths.addAll(firstConditionPath);

                Map<String, List<PolicyCondition>> policyConditionMap = new HashMap<>();
                Map<String, PolicyAction> policyActionMap = new HashMap<>();
                //4.输入所有path，将path转为Policy，同时记录和每一个Policy关联的PolicyCondition、PolicyAction以及各个Param，将这4种对象插入数据库。
                List<Policy> policies = convertPathToPolicy(domainName, taskName, fromState, intentNames, paths, policyConditionMap, policyActionMap, paramsMap);
                for (Policy policy : policies) {
                    policyService.insert(policy);                                                                       //插入policy

                    String policyName = policy.getPolicyName();
                    List<PolicyCondition> policyConditions = policyConditionMap.get(policyName);
                    if(policyConditions != null){
                        for (PolicyCondition policyCondition : policyConditions) {
                            policyCondition.setPolicyId(policy.getId());
                            policyConditionService.insert(policyCondition);                                             //插入policyCondition集合
                        }
                    }

                    PolicyAction policyAction = policyActionMap.get(policyName);
                    if(policyAction != null){
                        policyAction.setPolicyId(policy.getId());
                        policyActionService.insert(policyAction);                                                       //插入policyAction
                    }
                }
            }
        }

        Collection<Param> values = paramsMap.values();                                                                  //排序并插入Param集合
        List<Param> params = new ArrayList<>(values);
        Collections.sort(params, new Comparator<Param>() {
            @Override
            public int compare(Param o1, Param o2) {
                String o1key = o1.getParamName() + o1.getGroupNum();
                String o2key = o2.getParamName() + o2.getGroupNum();
                return o1key.compareTo(o2key);
            }
        });
        for (Param param : params) {
            paramService.insert(param);
        }
        System.out.println("Policy、PolicyCondition、PolicyAction、Param 初步数据生成完毕！");
    }

    private static List<StateNode> selectAllStatesFromDomainTask(String domainName, String taskName) {
        String queryForIntents = "match data=(s:State{domain:'" + domainName + "', task:'" + taskName + "'}) return s";
        List<Map<String, String>> stateNodeDataMaps = DataTool.getNodeDataMaps(queryForIntents);
        if(stateNodeDataMaps == null || stateNodeDataMaps.size() == 0) return new ArrayList<>();

        List<StateNode> stateNodes = new ArrayList<>();
        for (Map<String, String> nodeDataMap : stateNodeDataMaps) {
            if(nodeDataMap.get("_neo4j_labels").equals("[State]")){
                StateNode stateNode = new StateNode(
                        nodeDataMap.get("SUID"),
                        nodeDataMap.get("domain"),
                        nodeDataMap.get("task"),
                        nodeDataMap.get("name"),
                        nodeDataMap.get("_neo4j_labels"),
                        nodeDataMap.get("class"),
                        nodeDataMap.get("value"),
                        nodeDataMap.get("text"),
                        nodeDataMap.get("msg")
                );
                stateNodes.add(stateNode);
            }
        }
        return stateNodes;
    }

    private static List<IntentSetNode> selectAllIntentSetFromDTS(String domain, String task, String fromState) {
        String queryForIntents = "match data=(s1:State{domain:'" + domain + "', task:'" + task + "', value:'" + fromState + "'})-[]->(is:IntentSet{domain:'" + domain + "', task:'" + task + "'}) return is";
        List<Map<String, String>> intentSetNodeDataMaps = DataTool.getNodeDataMaps(queryForIntents);
        if(intentSetNodeDataMaps == null || intentSetNodeDataMaps.size() == 0) return new ArrayList<>();
        List<IntentSetNode> intentSetNodes = new ArrayList<>();
        for (Map<String, String> nodeDataMap : intentSetNodeDataMaps) {
            if(nodeDataMap.get("_neo4j_labels").equals("[IntentSet]")){
                IntentSetNode intentSetNode = new IntentSetNode(
                        nodeDataMap.get("SUID"),
                        nodeDataMap.get("domain"),
                        nodeDataMap.get("task"),
                        nodeDataMap.get("name"),
                        nodeDataMap.get("_neo4j_labels"),
                        nodeDataMap.get("class"),
                        nodeDataMap.get("value"),
                        nodeDataMap.get("text"),
                        nodeDataMap.get("msg")
                );
                intentSetNodes.add(intentSetNode);
            }
        }
        return intentSetNodes;
    }

    private static List<Path> findDirectToStatePath(String directToStatePathFromIntent) {
        List<Path> paths = new ArrayList<>();
        Session session = null;
        try {
            session = neo4jDriver.session();
            Result result = session.run(directToStatePathFromIntent);
            if (result != null && result.hasNext()) {
                List<Record> pathRecords = result.list();								//这里理应只有一条路径的，因为一个状态+意图，不结合任何条件，只应该有一条短路径。
                for (Record pathRecord : pathRecords) {
                    List<Pair<String, Value>> pathInfoPairs = pathRecord.fields();
                    Pair<String, Value> pathInfoPair = pathInfoPairs.get(0);            //此处断定pathInfoPairs里面只有一个元素，但如果以后发生特殊情况，则还是要遍历。
                    Value value = pathInfoPair.value();
                    Path path = value.asPath();
                    Node end = path.end();
                    String aClass = end.get("class").asString();
                    if(aClass.equals("State")){
                        paths.add(path);
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
        return paths;
    }

    private static List<Policy> convertPathToPolicy(String domainName, String taskName, String fromState, String intentNames, List<Path> paths,
                                                    Map<String, List<PolicyCondition>> policyConditionMap, Map<String, PolicyAction> policyActionMap, Map<String, Param> paramsMap) {
        if(paths == null || paths.size() == 0) return new ArrayList<>();
        List<Policy> policies = new ArrayList<>();
        for (Path path : paths) {
            Policy policy = new Policy();
            String uuid = "" + UUID.randomUUID();
            String policyName = "Policy_" + uuid;
            policy.setPolicyName(policyName);
            policy.setDomainName(domainName);
            policy.setTaskName(taskName);
            policy.setFromState(fromState);
            policy.setIntentNames(intentNames);
            policy.setEnable((byte)1);

            for (Path.Segment segment : path) {                       //取出当前Path的一个段Segment，每一只检测这一段的"头结点+关联"的整体结果，不检测这一段的尾结点条件。
                Node start = segment.start();
                Relationship relationship = segment.relationship();
                Node end = segment.end();

                String aClass = start.get("class").asString();
                if(aClass.equals("Condition")){
                    PolicyCondition policyCondition = new PolicyCondition();
                    String conditionName = start.get("name").asString();
                    policyCondition.setConditionName(conditionName);
                    if(relationship != null){
                        int conditionWhether = 0;
                        String whether = relationship.get("whether").asString();
                        if(whether.equals("Y")) conditionWhether = 1;
                        else if(whether.equals("")) conditionWhether = 0;
                        else if(whether.equals("N")) conditionWhether = -1;
                        policyCondition.setConditionWhether((byte)conditionWhether);
                    }
                    policyCondition.setConditionText(start.get("text").asString());
                    policyCondition.setEnable((byte)1);
                    policyCondition.setMsg("条件 -> " + conditionName + " (" + policyCondition.getConditionWhether() + ") : [ " + policyCondition.getConditionText() + " ]");

                    List<PolicyCondition> policyConditions = policyConditionMap.get(policy.getPolicyName());
                    if(policyConditions == null)  policyConditions = new ArrayList<>();
                    policyConditions.add(policyCondition);
                    policyConditionMap.put(policy.getPolicyName(), policyConditions);

                    String type = start.get("type").asString();
                    String param = start.get("param").asString();
                    if(paramsMap.get(param) == null){
                        //每一个变量添加两个获取方法
                        paramsMap.put(domainName + "_" + taskName + "_" + param + "_HTTP", new Param(null, domainName, taskName, param, 2, 1, "", 1,   "HTTP动作     -> " + type + " 类型变量:" + param ));
                        paramsMap.put(domainName + "_" + taskName + "_" + param + "_SCRIPT", new Param(null, domainName, taskName, param, 1, 2, "", 1, "SCRIPT动作 -> " + type + " 类型变量:" + param ));
                    }
                }else if(aClass.equals("State")){
                    policy.setToState(start.get("value").asString());
                    break;
                }

                String endClass = end.get("class").asString();
                if(endClass.equals("State")){
                    policy.setToState(end.get("value").asString());
                    break;
                }
            }

            policyActionMap.put(policy.getPolicyName(), new PolicyAction(null, "PolicyAction_" + uuid, null, 2, 2, "", 1,
                    "交互动作 -> 起始状态： " + fromState + " 意图：" + intentNames + " 到结束状态：" + policy.getToState()));
            policy.setMsg("策略 -> 起始状态： " + fromState + " 意图：" + intentNames + " 到结束状态：" + policy.getToState());
            policies.add(policy);
        }
        return policies;
    }

    private static List<Map<String, String>> getNodeDataMaps(String cypherSql) {
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


}
