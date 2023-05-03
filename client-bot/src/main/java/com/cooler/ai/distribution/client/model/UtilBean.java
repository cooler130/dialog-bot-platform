package com.cooler.ai.distribution.client.model;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.distribution.facade.constance.Constant;
import com.cooler.ai.distribution.facade.constance.PC;
import com.cooler.ai.distribution.facade.model.ClientInfo;
import com.cooler.ai.distribution.facade.model.DMRequest;
import com.cooler.ai.distribution.facade.model.LocationInfo;
import com.cooler.ai.distribution.facade.model.UserInfo;
import com.cooler.ai.distribution.EntityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UtilBean {

    private static final Logger logger = LoggerFactory.getLogger(UtilBean.class);


    /**
     * 创建DmRequest（里面有两种模式）
     * @param sessionId
     * @param query
     * @return
     */
    public static DMRequest createDmRequest(String sessionId, String query) {
        DMRequest dmRequest = new DMRequest();

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


        String[] split = query.split("->");
        String queryType = null;
        String sentence = null;
        if(split.length == 1){                              //如果前面没有类型，默认为文本
            queryType = EntityConstant.QUERYTYPE_TEXT;
            sentence = split[0];
        }else{
            queryType = split[0];
            sentence = split[1];
        }
        dmRequest.setRequestType(queryType);

        if(EntityConstant.LANGUAGE_QUERYTYPES.contains(queryType)){
            dmRequest.setQuery(sentence);
        }else if(EntityConstant.NON_LANGUAGE_QUERYTYPES.contains(queryType)){
            dmRequest.setQuery(queryType + " 型数据");
            Map<String, String> metaMap = createMetaMap(sentence);
            dmRequest.setExtendInfo(metaMap);
        }
        return dmRequest;
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
        metaMap.put(Constant.BIZ_PARAM_MAP, JSON.toJSONString(slotValueMap));
        return metaMap;
    }

}
