package com.cooler.ai.platform.model;

public class EntityConstant {


    //--------------------------------------------------------------------------兜底相关常量(系统默认数据，每个类中会定义相关记录，数据库中也有相关记录）

    public static final String NO_INTENT = "no_intent";                             //空意图名称
    public static final String UNKNOWN_INTENT = "unknown_intent";                   //未知意图
    public static final String ANY_INTENT = "any_intent";                           //任意意图

    public static final String START_STATE = "global_start";                        //开始状态
    public static final String END_STATE = "global_end";                            //结束状态
    public static final String ERROR_STATE = "global_error";                         //系统错误状态
    public static final String ANY_STATE = "global_any";                            //任意状态的状态


    public static final int DEFAULT_POLICY_ID = 1;                                  //兜底策略ID

    public static final int DEFAULT_ACTION_ID = 1;                                  //全局兜底动作ID



}
