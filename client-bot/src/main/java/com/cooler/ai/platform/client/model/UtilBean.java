package com.cooler.ai.platform.client.model;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilBean {

    private static final Logger logger = LoggerFactory.getLogger(UtilBean.class);

    /**
     * 根据query检测此问句的queryType
     * @param query
     * @return
     */
    public static String checkQueryType(String query){
        String queryType = null;
        if(query.startsWith("signal")){
            queryType = Constant.QUERYTYPE_SIGNAL;
        }else if(query.startsWith("action")){
            queryType = Constant.QUERYTYPE_ACTION;
        }else if(query.startsWith("transform_intent")){
            queryType = Constant.QUERYTYPE_TRANSFORM_INTENT;
        }else{
            queryType = Constant.QUERYTYPE_TEXT;
        }
        return queryType;
    }

    /**
     * 创建DmRequest（里面有两种模式）
     * @param sessionId
     * @param queryType
     * @param query
     * @return
     */
    public static DMRequest createDmRequest(String sessionId, String queryType, String query) {
        DMRequest dmRequest = new DMRequest();

        dmRequest.setDmType(Constant.MODEL_RDB);                                          //RDB作为调试模式

        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setChannel("000");
        clientInfo.setClientId("CID_8A831A1489014520BAB004F70C452396");
        clientInfo.setClientType("h5");
        clientInfo.setClientName("cooler_iphone");
        dmRequest.setClientInfo(clientInfo);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("TEST_USER_ID_1");
        userInfo.setUserName("cooler");
        dmRequest.setUserInfo(userInfo);

        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setCityName("北京");
        locationInfo.setLatitude(0);
        locationInfo.setLongitude(0);
        dmRequest.setLocationInfo(locationInfo);

        dmRequest.setSessionId(sessionId);
        dmRequest.setExtendInfo(new HashMap<>());

        dmRequest.setRequestType(queryType);
        if(Constant.LANGUAGE_QUERYTYPES.contains(queryType)){
            NLUData nluData = nluParse(query);                              //query形式要为NluMockData中存在的句子
            dmRequest.setNluData(nluData);
        }else if(Constant.NON_LANGUAGE_QUERYTYPES.contains(queryType)){
            query = query.replace(queryType + "->", "");   //query形式为    signal:testcase|no_intent|2|signal:init@slotName1:slotValue1@slotName2:slotValue2
            Map<String, String> metaMap = createMetaMap(query);
            dmRequest.setExtendInfo(metaMap);
        }
        return dmRequest;
    }

    /**
     * 通过query构建NLUData（语言交互）
     * @param query
     * @return
     */
    public static NLUData nluParse(String query){
        if(query.trim().equals("")) return null;
        NLUData nluData = new NLUData();
        List<DomainInfo> domainInfos = new ArrayList<>();
        DomainInfo domainInfo = NluMockData.nluMockMap.get(query);

        if(domainInfo != null) {
            domainInfos.add(domainInfo);
            nluData.setResult(domainInfos);
            return nluData;
        }else{
            logger.error("没有相关的nlu数据！");
        }
        return null;
    }

    /**
     * 通过query建立metaMap（非语言交互）
     * query形式：                 testcase|no_intent|2|signal:init@slotName1:slotValue1@slotName2:slotValue2
     * @param query
     * @return
     */
    public static Map<String, String> createMetaMap(String query){
        String[] splits = query.split("\\|");

        String domainName = splits[0];
        String intentName = splits[1];
        String taskName = splits[2];

        String slotValuesStr = splits[3];
        String[] slotKVSplits = slotValuesStr.split("@");
        Map<String, String> slotValueMap = null;
        if(slotKVSplits.length > 0){
            slotValueMap = new HashMap<>();
            for (String slotKVSplit : slotKVSplits) {
                String[] kvs = slotKVSplit.split(":");
                String key = kvs[0];
                String value = kvs[1];
                slotValueMap.put(key, value);
            }
        }

        Map<String, String> metaMap = new HashMap<>();
        metaMap.put(PC.DOMAIN_NAME, domainName);
        metaMap.put(PC.TASK_NAME, taskName);
        metaMap.put(PC.INTENT_NAME, intentName);
        metaMap.put(Constant.PARAM_VALUE_MAP, JSON.toJSONString(slotValueMap));
        return metaMap;
    }

}
