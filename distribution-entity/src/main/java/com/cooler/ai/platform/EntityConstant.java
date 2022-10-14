package com.cooler.ai.platform;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityConstant {

    //--------------------------------------------------------------------------兜底相关常量(系统默认数据，每个类中会定义相关记录，数据库中也有相关记录）
    public static final int NO_INTENT_ID = 1;                                       //空意图ID
    public static final int UNKNOWN_INTENT_ID = 2;                                  //未知意图ID
    public static final int ANY_INTENT_ID = 3;                                      //任意意图ID

    public static final String NO_DOMAIN = "no_domain";                             //空NLU领域
    public static final String GUIDE_DOMAIN = "guide";                                     //引导领域

    public static final String NO_INTENT = "no_intent";                             //空意图名称
    public static final String UNKNOWN_INTENT = "unknown_intent";                   //未知意图
    public static final String ANY_INTENT = "any_intent";                           //任意意图

    public static final int NO_TASK_ID = 1;                                         //空任务ID
    public static final String NO_TASK = "no_task";                                    //空任务

    public static final int GLOBAL_START_ID = 1;                                     //开始状态的ID
    public static final int GLOBAL_END_ID = 2;                                       //结束状态的ID
    public static final int GLOBAL_ERROR_ID = 3;                                     //系统错误状态ID
    public static final int GLOBAL_ANY_ID = 4;                                       //任意状态的状态ID

    public static final String GLOBAL_START = "global_start";                                     //开始状态
    public static final String GLOBAL_END = "global_end";                                       //结束状态
    public static final String GLOBAL_ERROR = "global_error";                                     //系统错误状态
    public static final String GLOBAL_ANY = "global_any";                                       //任意状态的状态


    public static final int START_END_TRANSITION_ID = 1;                            //全局的从开始到结束的变迁的ID

    public static final int PERMISSION_CONDITION_RULE_ID = 1;                       //全局允许通行规则ID
    public static final int PROHIBITION_CONDITION_RULE_ID = 2;                      //全局禁止通行规则ID

    public static final int GLOBAL_NULL_TASK_ID = 1;                                //全局空任务ID

    public static final int DEFAULT_POLICY_ID = 1;                                  //兜底策略ID

    public static final int DEFAULT_ACTION_ID = 1;                                  //全局兜底动作ID
    public static final int INQUIRY_ACTION_ID = 2;                                  //全局询问动作ID

    public static final String DEFAULT_ACTION_PROCESSCODE = "default";              //兜底动作的processCode
    public static final String INQUIRY_ACTION_PROCESSCODE = "inquiry";              //询问动作的processCode
    public static final String NONE_ACTION_PROCESSCODE = "none";                    //空动作的processCode

    public static final Map<String, String> globalMap = new ConcurrentHashMap<>();  //为了支持以后的热加载，还是用ConcurrentHashMap

    //--------------------------------------------------------------------------逻辑运算符
    public static final int AND = 1;
    public static final int OR = 0;
    public static final int NOT = -1;
    public static final int XOR = -2;


}
