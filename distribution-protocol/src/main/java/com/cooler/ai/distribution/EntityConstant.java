package com.cooler.ai.distribution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EntityConstant {

    //--------------------------------------------------------------------------兜底相关常量(系统默认数据，每个类中会定义相关记录，数据库中也有相关记录）

    public static final String NO_DOMAIN = "no_domain";                             //空NLU领域
    public static final String GUIDE_DOMAIN = "guide";                              //引导领域

    public static final int NO_INTENT_ID = 1;                                       //空意图ID
    public static final String NO_INTENT = "no_intent";                             //空意图名称
    public static final String UNKNOWN_INTENT = "unknown_intent";                   //未知意图

    public static final String NO_TASK = "no_task";                                 //空任务

    public static final String GLOBAL_START = "global_start";                                     //开始状态
    public static final String GLOBAL_END = "global_end";                                       //结束状态
    public static final String GLOBAL_ERROR = "global_error";                                     //系统错误状态
    public static final String GLOBAL_ANY = "global_any";                                       //任意状态的状态

    //--------------------------------------------------------------------------访问类型
    public static final String QUERYTYPE_SIGNAL = "signal";                     //信号
    public static final String QUERYTYPE_ACTION = "action";                     //行为（特指点击行为）
    public static final String QUERYTYPE_TRANSFORM_INTENT = "transform_intent"; //转移意图

    public static final String QUERYTYPE_TEXT = "text";                         //文本
    public static final String QUERYTYEP_IMAGE = "image";                       //图片（为了支持表情等富文本）
    public static final String QUERYTYPE_MEDIA = "media";                       //声音
    public static final String QUERYTYPE_UNSUPPORTED = "unsupported";           //不支持的类型（无法识别的类型）

    public static final Set<String> NON_LANGUAGE_QUERYTYPES = new HashSet(Arrays.asList(QUERYTYPE_SIGNAL, QUERYTYPE_ACTION, QUERYTYPE_TRANSFORM_INTENT));
    public static final Set<String> LANGUAGE_QUERYTYPES = new HashSet(Arrays.asList( QUERYTYPE_TEXT, QUERYTYEP_IMAGE, QUERYTYPE_MEDIA, QUERYTYPE_UNSUPPORTED));

}
