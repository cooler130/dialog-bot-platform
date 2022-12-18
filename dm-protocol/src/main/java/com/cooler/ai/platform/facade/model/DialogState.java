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
     * 渠道标识
     */
    private String channel = null;

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
        ModelState<Map<String, String>> paramMapMS = getParamMapMSByType(paramType);
        if(paramMapMS == null){                     //传递的paramType不在 {PLATFORM_PARAM, SLOT_PARAM, CUSTOM_PARAM, BIZ_PARAM}，就加到业务变量池
            paramMapMS = getParamMapMSByType(Constant.BIZ_PARAM);
        }
        Map<String, String> paramMap = paramMapMS.getT();
        if(paramType.equals(Constant.PLATFORM_PARAM)){                      //首选系统变量
            key = "$" + key + "$";
        }else if(paramType.equals(Constant.SLOT_PARAM)){
            key = "@" + key + "@";
        }else if(paramType.equals(Constant.CUSTOM_PARAM)){
            key = "#" + key + "#";
        } else if(paramType.equals(Constant.BIZ_PARAM)){                                                                //默认是BizMap
            key = "%" + key + "%";
        }
        paramMap.put(key, value);
    }

    /**
     * 批量装载
     * @param params
     * @param paramType
     */
    public void addToParamValueMap(Map<String, String> params, String paramType){
        ModelState<Map<String, String>> paramMapMS = getParamMapMSByType(paramType);
        if(paramMapMS == null){                     //传递的paramType不在 {PLATFORM_PARAM, SLOT_PARAM, CUSTOM_PARAM, BIZ_PARAM}，就加到业务变量池
            paramMapMS = getParamMapMSByType(Constant.BIZ_PARAM);
        }
        Map<String, String> paramMap = paramMapMS.getT();
        paramMap.putAll(params);
    }

    /**
     * 从dialogState中的 BIZ_PARAM_MAP 中取出指定类型的值
     * @param key
     * @param paramType
     * @return
     */
    public String getParamValue(String key, String paramType) {
        ModelState<Map<String, String>> paramMapMS = getParamMapMSByType(paramType);
        if(paramMapMS == null) return null;                 //传递的paramType不在 {PLATFORM_PARAM, SLOT_PARAM, CUSTOM_PARAM, BIZ_PARAM}
        if(paramType.equals(Constant.PLATFORM_PARAM)){                      //首选系统变量
            key = "$" + key + "$";
        }else if(paramType.equals(Constant.SLOT_PARAM)){
            key = "@" + key + "@";
        }else if(paramType.equals(Constant.CUSTOM_PARAM)){
            key = "#" + key + "#";
        } else if(paramType.equals(Constant.BIZ_PARAM)){                                                                //默认是BizMap
            key = "%" + key + "%";
        }
        Map<String, String> paramMap = paramMapMS.getT();
        String paramValue = paramMap.get(key);
        return paramValue;
    }

    private ModelState<Map<String, String>> getParamMapMSByType(String paramType){
        ModelState<Map<String, String>> paramMapMS = null;
        if(paramType.equals(Constant.PLATFORM_PARAM)){                      //首选系统变量
            paramMapMS = modelStateMap.get(Constant.PLATFORM_PARAM_MAP);
            if(paramMapMS == null){
                paramMapMS = new BaseModelState<>();
                paramMapMS.setT(new HashMap<>());
                modelStateMap.put(Constant.PLATFORM_PARAM_MAP, paramMapMS);
            }
        }else if(paramType.equals(Constant.SLOT_PARAM)){
            paramMapMS = modelStateMap.get(Constant.SLOT_PARAM_MAP);
            if(paramMapMS == null){
                paramMapMS = new BaseModelState<>();
                paramMapMS.setT(new HashMap<>());
                modelStateMap.put(Constant.SLOT_PARAM_MAP, paramMapMS);
            }
        }else if(paramType.equals(Constant.CUSTOM_PARAM)){
            paramMapMS = modelStateMap.get(Constant.CUSTOM_PARAM_MAP);
            if(paramMapMS == null){
                paramMapMS = new BaseModelState<>();
                paramMapMS.setT(new HashMap<>());
                modelStateMap.put(Constant.CUSTOM_PARAM_MAP, paramMapMS);
            }
        } else if(paramType.equals(Constant.BIZ_PARAM)){                                                                //默认是BizMap
            paramMapMS = modelStateMap.get(Constant.BIZ_PARAM_MAP);
            if(paramMapMS == null){
                paramMapMS = new BaseModelState<>();
                paramMapMS.setT(new HashMap<>());
                modelStateMap.put(Constant.BIZ_PARAM_MAP, paramMapMS);
            }
        }
        return paramMapMS;
    }

}
