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
     * 此方法的key没有带上各类参数的标示，加入时根据paramType来判断类型，key做一些处理
     * @param key
     * @param value
     * @param paramType
     */
    public void addToParamValueMap(String key, String value, String paramType){
        ModelState<Map<String, String>> slotValueMapMS = modelStateMap.get(Constant.PARAM_VALUE_MAP);
        if(slotValueMapMS == null){
            slotValueMapMS = new BaseModelState<>();
            slotValueMapMS.setT(new HashMap<>());
        }
        Map<String, String> slotValueMap = slotValueMapMS.getT();
        switch (paramType){
            case Constant.SLOT_PARAM : {
                slotValueMap.put("@" + key + "@", value);
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
                slotValueMap.put("%" + key + "%", value);                   //默认是设置为业务变量
            }
        }
    }

    /**
     * 从dialogState中的 PARAM_VALUE_MAP 中取出指定类型的值
     * @param key
     * @param paramType
     * @return
     */
    public String getParamValue(String key, String paramType) {
        ModelState<Map<String, String>> slotValueMapMS = modelStateMap.get(Constant.PARAM_VALUE_MAP);
        if(slotValueMapMS == null){
            slotValueMapMS = new BaseModelState<>();
            slotValueMapMS.setT(new HashMap<>());
            modelStateMap.put(Constant.PARAM_VALUE_MAP, slotValueMapMS);
            return null;
        }
        Map<String, String> slotValueMap = slotValueMapMS.getT();
        return getParamValueOfAMap(key, paramType, slotValueMap);
    }

    /**
     * 从指定Map中取出指定类型的值
     * @param key
     * @param paramType
     * @param paramMap
     * @return
     */
    public String getParamValueOfAMap(String key, String paramType, Map<String, String> paramMap) {
        if(paramMap == null) return null;
        switch (paramType) {
            case Constant.SLOT_PARAM: {
                return paramMap.get("@" + key + "@");
            }
            case Constant.CUSTOM_PARAM: {
                return paramMap.get("#" + key + "#");
            }
            case Constant.PLATFORM_PARAM: {
                return paramMap.get("$" + key + "$");
            }
            case Constant.BIZ_PARAM: {
                return paramMap.get("%" + key + "%");
            }
            default: {
                String value = paramMap.get("@" + key + "@");
                if (value == null) {
                    value = paramMap.get("%" + key + "%");
                }
                if (value == null) {
                    value = paramMap.get("#" + key + "#");
                }
                if (value == null) {
                    value = paramMap.get("$" + key + "$");
                }
                return value + "";
            }
        }
    }
}
