package com.cooler.ai.platform.facade.model;

import com.cooler.ai.platform.facade.constance.Constant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class DialogState implements Serializable{

    /**
     * SessionId
     */
    private String sessionId = null;

    /**
     * 客户端ID
     */
    private String clientId = null;

    /**
     * 用户ID
     */
    private String userId = null;

    /**
     * 交互数据
     */
    private String interactiveData = null;

    /**
     * 领域名称
     */
    private String domainName = null;

    /**
     * 任务名称
     */
    private String taskName = null;

    /**
     * 意图名称
     */
    private String intentName = null;

    /**
     * 本DialogState是哪一个bot产生的
     */
    private String botName = null;

    /**
     * 总体对话轮次
     */
    private int totalTurnNum = -1;

    /**
     * 领域对话轮次
     */
    private int domainTurnNum = -1;

    /**
     * 话题对话轮次
     */
    private int domainTaskTurnNum = -1;

    /**
     * DM中的意图集合
     */
    private Map<String, ModelState> modelStateMap = null;


    public <T> void addToModelStateMap(String key, T t){
        ModelState baseModelState = new BaseModelState();
        baseModelState.setT(t);
        if(modelStateMap == null){
            modelStateMap = new HashMap<>();
        }
        modelStateMap.put(key, baseModelState);
    }

    public <T> T getFromModelStateMap(String key, Class<T> clazz){
        ModelState modelState = modelStateMap.get(key);
        if(modelState != null){
            Object o = modelState.getT();
            T t = clazz.cast(o);
            return t;
        }
        return null;
    }

    /**
     * 此方法的key已经带上了各类参数的标示，所以可以直接加入到PARAM_VALUE_MAP中
     * @param key
     * @param value
     */
    public void addToParamValueMapDirectly(String key, String value){
        ModelState<Map<String, String>> slotValueMapMS = modelStateMap.get(Constant.PARAM_VALUE_MAP);
        if(slotValueMapMS == null){
            slotValueMapMS = new BaseModelState<>();
            slotValueMapMS.setT(new HashMap<String, String>());
        }
        Map<String, String> slotValueMap = slotValueMapMS.getT();
        slotValueMap.put(key, value);
    }

    /**
     * 此方法的key没有带上各类参数的标示，加入时根据paramType来判断类型，key做一些处理
     * @param key
     * @param value
     * @param paramType
     */
    public void addToParamValueMap(String key, String value, int paramType){
        ModelState<Map<String, String>> slotValueMapMS = modelStateMap.get(Constant.PARAM_VALUE_MAP);
        if(slotValueMapMS == null){
            slotValueMapMS = new BaseModelState<>();
            slotValueMapMS.setT(new HashMap<String, String>());
        }
        Map<String, String> slotValueMap = slotValueMapMS.getT();
        switch (paramType){
            case Constant.SLOT_PARAM : {
                slotValueMap.put(key, value);
                break;
            }
            case Constant.CUSTOM_PARAM : {
                slotValueMap.put("#" + key + "#", value);
                break;
            }
            case Constant.PLATFORM_PARAM: {
                slotValueMap.put("$" + key + "$", value);
                break;
            }
            case Constant.BIZ_PARAM: {
                slotValueMap.put("%" + key + "%", value);
                break;
            }
            default:{
                slotValueMap.put(key, value);
            }
        }
    }

    public String getParamValue(String key, int paramType){
        ModelState<Map<String, String>> slotValueMapMS = modelStateMap.get(Constant.PARAM_VALUE_MAP);
        if(slotValueMapMS == null){
            slotValueMapMS = new BaseModelState<>();
            slotValueMapMS.setT(new HashMap<String, String>());
            modelStateMap.put(Constant.PARAM_VALUE_MAP, slotValueMapMS);
            return null;
        }
        Map<String, String> slotValueMap = slotValueMapMS.getT();
        switch (paramType){
            case Constant.SLOT_PARAM : {
                return slotValueMap.get(key);
            }
            case Constant.CUSTOM_PARAM : {
                return slotValueMap.get("#" + key + "#");
            }
            case Constant.PLATFORM_PARAM: {
                return slotValueMap.get("$" + key + "$");
            }
            case Constant.BIZ_PARAM: {
                return slotValueMap.get("%" + key + "%");
            }
            case Constant.SLOT_BIZ_PARAM: {
                String slotValue = slotValueMap.get(key);
                return (slotValue != null) ? slotValue : slotValueMap.get("%" + key + "%");
            }
            case Constant.BIZ_SLOT_PARAM: {
                String bizValue = slotValueMap.get("%" + key + "%");
                return (bizValue != null) ? bizValue : slotValueMap.get(key);
            }
            default:{
                String value = slotValueMap.get(key);
                if(value == null){
                    value = slotValueMap.get("%" + key + "%");
                }
                if(value == null){
                    value = slotValueMap.get("#" + key + "#");
                }
                if(value == null){
                    value = slotValueMap.get("$" + key + "$");
                }
                return value;
            }
        }
    }

    /**
     * 从槽位值集、合成值集中取得某个key下的值，如果没有取到，则返回默认值defaultValue
     * @param key           查询key
     * @param defaultValue  默认值
     * @return
     */
    public String getParamValueOrDefault(String key, int paramType, String defaultValue){
        String value = getParamValue(key, paramType);
        if(value == null){
            value = defaultValue;
        }
        return value;
    }

}
