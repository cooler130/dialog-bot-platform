package com.cooler.ai.dm;



import com.cooler.ai.distribution.entity.DataVersion;
import com.cooler.ai.distribution.entity.Intent;
import com.cooler.ai.distribution.entity.NLUIntent;
import com.cooler.ai.distribution.service.DataVersionService;
import com.cooler.ai.distribution.service.IntentService;
import com.cooler.ai.distribution.service.NLUIntentService;
import com.cooler.ai.dm.model.IntentSetNode;
import com.cooler.ai.dm.model.StateNode;
import com.cooler.ai.dm.entity.Param;
import com.cooler.ai.dm.entity.Policy;
import com.cooler.ai.dm.entity.PolicyAction;
import com.cooler.ai.dm.entity.PolicyCondition;
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

    public static final String domainName = "boss_hiring";
    public static final String taskName = "collect_resume";
    public static final String versionUpdateItems = "数据初始化";                //更新点先设置到此处
    public static final boolean smallVersionUpdate = true;              //是否小版本升级（小版本升级 oldVerion + 0.1，大版本升级 float(oldVerion) + 1

    public static final int estimatedIntentCount = 100;


    public static void main(String[] args) {

        BeanFactory beanFactory_DC = new ClassPathXmlApplicationContext("applicationContext-DC.xml");
        String version = createDataVersion(beanFactory_DC);
        System.out.println("1.DataVersion 数据生成完毕！");

        BeanFactory beanFactory_DM = new ClassPathXmlApplicationContext("applicationContext-DM.xml");
        Map<String, String> intentMap = createPolicyBizData(beanFactory_DM, version);
        System.out.println("2.DataVersion、Policy、PolicyCondition、PolicyAction、Param 初步数据生成完毕！");

        createIntentBizData(beanFactory_DC, version, intentMap);
        System.out.println("3.nluIntent、intent 数据生成完毕！");
    }

    //-----------------以下为3个子步骤

    private static String createDataVersion(BeanFactory beanFactory_DC) {
        DataVersionService dataVersionService = (DataVersionService) beanFactory_DC.getBean("dataVersionService");

        DataVersion latestDataVersion = dataVersionService.selectLatestVersion(domainName, taskName);
        Float latestVersionCode = 1.0f;
        if(latestDataVersion != null && latestDataVersion.getVersionCode() != null){
            latestVersionCode = latestDataVersion.getVersionCode();
        }
        Float versionCode = smallVersionUpdate ? latestVersionCode + 0.1f : (float)Math.floor(latestVersionCode) + 1.0f;
        String versionName = "V" + versionCode;
//        String newVersionName = "xxxVersion";
        //新建的DataVersion，自己将升级项目点填到versionUpdateItemes变量，默认在线、不稳定。
        DataVersion newDataVersion = new DataVersion(null, versionName, versionCode, domainName, taskName, (byte)1, (byte)0, versionUpdateItems, new Date(), new Date());
        dataVersionService.insert(newDataVersion);
        return versionName;
    }

    private static Map<String, String> createPolicyBizData(BeanFactory beanFactory_DM, String version) {
        PolicyService policyService = (PolicyService) beanFactory_DM.getBean("policyService");
        PolicyConditionService policyConditionService = (PolicyConditionService) beanFactory_DM.getBean("policyConditionService");
        PolicyActionService policyActionService = (PolicyActionService) beanFactory_DM.getBean("policyActionService");
        ParamService paramService = (ParamService) beanFactory_DM.getBean("paramService");

        //0.设置好默认策略和默认动作
        Policy defaultPolicy = new Policy(null,"default_policy", domainName, taskName, "global_any", "no_intent", "global_error",  version, (byte) 1, "默认兜底策略");
        policyService.insert(defaultPolicy);
        PolicyAction defaultPolicyAction = new PolicyAction(null, "default_action", defaultPolicy.getId(), 2, 1, "抱歉！没有理解您的意思，请换个说法再说一次吧！", domainName, taskName, version, 1, "默认兜底动作");
        policyActionService.insert(defaultPolicyAction);

        //1.根据domain、task查询所有的状态，开始遍历这些状态；
        List<StateNode> stateNodes = DataTool.selectAllStatesFromDomainTask(domainName, taskName);
        Map<String, Param> paramsMap = new HashMap<>();

        Map<String, String> intentMap = new HashMap<>();                                    //为第三步提供数据
        //2.查询一个状态出发的所有意图；
        for (StateNode stateNode : stateNodes) {
            String fromState = stateNode.getValue();
            List<IntentSetNode> intentSetNodes = DataTool.selectAllIntentSetFromDTS(domainName, taskName, fromState);

            for (IntentSetNode intentSetNode : intentSetNodes) {
                String suid = intentSetNode.getSUID();

                String intentNames = intentSetNode.getValue();
                String intentSetText = intentSetNode.getText();
                String[] intentNameArray = intentNames.split(",");
                String[] intentTextArray = intentSetText.split(",");
                int size = intentNameArray.length;
                for (int i = 0; i < size; i++) {
                    String intent_i = intentNameArray[i].trim();
                    if(intent_i != null && intent_i.length() > 0){
                        int textLength = intentTextArray.length;
                        if(i < textLength - 1){
                            intentMap.put(intent_i, intentTextArray[i].trim());
                        }else{
                            intentMap.put(intent_i, intent_i);
                        }
                    }
                }

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
                List<Policy> policies = convertPathToPolicy(domainName, taskName, version, fromState, intentNames, paths, policyConditionMap, policyActionMap, paramsMap);
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

        return intentMap;
    }

    private static void createIntentBizData(BeanFactory beanFactory_DC, String version, Map<String, String> intentMap) {
        IntentService intentService = (IntentService) beanFactory_DC.getBean("intentService");
        NLUIntentService nluIntentService = (NLUIntentService) beanFactory_DC.getBean("nluIntentService");

        //1.intentService先搜索domainName+intentName + enable之下所有记录，形成 Map<domainName_intentName, Intent>
        //2.遍历所有value，匹配intentName，匹配上则将本此的Intent中的taskame加进taskNames，新增一个记录，将旧intent软删除enable=-1。

        if(intentMap != null && intentMap.size() > 0){
            for (String intentName : intentMap.keySet()) {
                Intent intent = new Intent(null, intentName, domainName, taskName, 1, version, (byte) 1, intentMap.get(intentName));
                intentService.insert(intent);

                NLUIntent nluIntent = new NLUIntent(null, intentName, domainName, 1, intent.getId(), version, (byte) 1, intentMap.get(intentName));
                nluIntentService.insert(nluIntent);
            }
        }
        Integer maxIntentId = intentService.selectMaxId();
        Integer newIntentId = (int)(100 * Math.floor(maxIntentId / 100)) + 100;
        Intent gapIntent = new Intent(newIntentId, "--", "--", "--", -1, "--", (byte) 0, "领域分界 - 前领域预留100");
        intentService.insert(gapIntent);

        Integer maxNLUInetentId = intentService.selectMaxId();
        Integer newNLUIntentId = (int)(100 * Math.floor(maxNLUInetentId / 100)) + 100;
        NLUIntent nluIntent = new NLUIntent(newNLUIntentId, "--", "--", -1, gapIntent.getId(), "--", (byte) 0, "领域分界 - 前领域预留100");
        nluIntentService.insert(nluIntent);

    }

    //-----------------以下为查询neo4j数据方法

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

    private static List<Policy> convertPathToPolicy(String domainName, String taskName, String version, String fromState, String intentNames, List<Path> paths,
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
            policy.setVersion(version);
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
                    policyCondition.setDomainName(domainName);
                    policyCondition.setTaskName(taskName);
                    policyCondition.setVersion(version);
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
                        paramsMap.put(domainName + "_" + taskName + "_" + version + "_" + param + "_HTTP", new Param(null, domainName, taskName, param, 2, 1, "", version,  1,   "HTTP动作     -> " + type + " 类型变量:" + param ));
                        paramsMap.put(domainName + "_" + taskName + "_" + version + "_" + param + "_SCRIPT", new Param(null, domainName, taskName, param, 1, 2, "", version,  1,   "SCRIPT动作     -> " + type + " 类型变量:" + param ));
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

            policyActionMap.put(policy.getPolicyName(), new PolicyAction(null, "PolicyAction_" + uuid, null, 2, 2, "", domainName, taskName, version, 1,
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
