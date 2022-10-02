package com.cooler.ai.platform.facade.constance;

import java.util.*;

public class Constant {

    public static final int MAX_BACKUP_TURN_COUNT = 5;                          //最多回溯多少轮对话
    public static final int MAX_DOMAIN_DATA_COUNT = 1;                          //最多获取多少个领域业务数据

    public static final String MODEL_RDB = "model_rdb";                         //关系型数据库模式（使用mysql，通常用于调试）
    public static final String MODEL_KVDB = "model_json";                       //json数据模式（通常用于线上，使用json文件，其是在关系数据库基础上生成的json文件）
    public static final String MODEL_GDB = "model_gdb";                         //图数据库模式（当前使用neo4j）
    public static final String MODEL_TREE = "model_tree";                       //树数据模式（当前使用xmind）

    //--------------------------------------------------------------------------意图类型
    public static final int DEFAULT_INTENT = -1;                                //默认兜底意图类型
    public static final int SYSTEM_INTENT = 0;                                  //系统意图类型（当前包括3类：no_intent、unknown_intent、any_intent）
    public static final int LEVEL_ONE_INTENT = 1;                               //一级 NLU语义解析的普通意图
    public static final int LEVEL_TWO_INTENT = 2;                               //二级 NLU语义解析的普通意图

    //--------------------------------------------------------------------------访问类型
    public static final String QUERYTYPE_SIGNAL = "signal";
    public static final String QUERYTYPE_ACTION = "action";
    public static final String QUERYTYPE_TRANSFORM_INTENT = "transform_intent";

    public static final String QUERYTYPE_TEXT = "text";
    public static final String QUERYTYEP_IMAGE = "image";
    public static final String QUERYTYPE_MEDIA = "media";
    public static final String QUERYTYPE_UNSUPPORTED = "unsupported";

    public static final Set<String> NON_LANGUAGE_QUERYTYPES = new HashSet(Arrays.asList(QUERYTYPE_SIGNAL, QUERYTYPE_ACTION, QUERYTYPE_TRANSFORM_INTENT));
    public static final Set<String> LANGUAGE_QUERYTYPES = new HashSet(Arrays.asList( QUERYTYPE_TEXT, QUERYTYEP_IMAGE, QUERYTYPE_MEDIA, QUERYTYPE_UNSUPPORTED));

    //--------------------------------------------------------------------------消息类型
    public static final String MSG_TEXT = "text";
    public static final String MSG_IMAGE = "image";
    public static final String MSG_BUBBLE = "bubble";
    public static final String MSG_DATA = "data";
    public static final String MSG_TRANSFER = "transfer";
    public static final String MSG_OTHER = "other";

    //--------------------------------------------------------------------------保存起来的数据
    public static final String DOMAIN_TASK_DATA = "domainTaskData";
    public static final String DIALOG_STATE = "dialogState";

    //下面为dialogState中ModelStateMap包含的5个数据
    public static final String SLOT_STATE_MAP = "slotStateMap";                 //槽位状态集标示（从NLU结果解析获取）
    public static final String UNKNOWN_SLOT_STATE_MAP = "unknownSlotStateMap";  //未识别槽位状态集标示（从NLU结果解析获取）
    public static final String PARAM_VALUE_MAP = "paramValueMap";               //4类数据存放处
    public static final String INTENT = "intent";                               //本轮意图对象
    public static final String TRANSFORM_INTENT = "transformIntent";            //本轮转义意图对象
    public static final String SO_DOMAIN_DECISION_MAP = "soDomainDecisionMap";       //（SlotOperate阶段记录）记录在多domain进行的决策过程种产生的决策数据
    public static final String DST_TRANSITION_CHECK_MAP = "dstTransitionCheckMap";    //（DST阶段记录）记录在多个变迁中，各个变迁的检测过程数据

    public static final String DOMAIN_DATAS = "domainDatas";                    //槽位操作记录（历史槽值集合、本轮得到的槽值集合、替换记录、最终确认的槽位集合）
    public static final String DOMAIN_INDICATORS = "domainIndicators";          //领域决策过程记录（各个领域所获取的比较特征数据，以及比较数据集合）

    //--------------------------------------------------------------------------参数类型
    public static final int UNKNOWN_PARAM = -1;                                 //未知参数
    public static final int NULL_PARAM = 0;                                     //空参数

    public static final int SLOT_PARAM = 1;                                     //槽位参数
    public static final int CUSTOM_PARAM = 2;                                   //定制/用户参数（首尾有#，包含用户请求数据、用户定制化数据）
    public static final int PLATFORM_PARAM = 3;                                 //平台参数（首尾有$，有限的几个：sameDomain、currentStateId、dmDomain、nluDomain、intentId、intentName）
    public static final int BIZ_PARAM = 4;                                      //业务参数（首尾有%，由以上参数调接口得来，跟业务相关）

    public static final int SLOT_BIZ_PARAM = 5;                                 //先槽位后业务参数
    public static final int BIZ_SLOT_PARAM = 6;                                 //先业务后槽位参数

    //--------------------------------------------------------------------------4类参数检测类型
    public static final int NO_CHECK = 0;                                       //无检测（自然通过或禁止）
    public static final int HAS_VALUE = 1;                                      //是否有值
    public static final int BEYOND_THRESHOLD = 2;                               //是否不小于置信阈值（置信度为1则代表用户确认）
    public static final int HIT_RELATION = 3;                                   //是否满足规定关系检测
    public static final int HIT_FUNCTION = 4;                                   //是否满足规定函数检测
    public static final int BEYOND_THRESHOLD_HIT_RELATION = 5;                  //是否不小于置信度阈值并满足规定关系检测
    public static final int BEYOND_THRESHOLD_HIT_FUNCTION = 6;                  //是否不小于置信度阈值并满足规定函数检测

    //--------------------------------------------------------------------------被检测值名为空值的默认值
    public static final String NONE_VALUE = "none";                             //系统认定的空值，即在规则中，如果一个检测值无值，则强制赋予none字符串

    //--------------------------------------------------------------------------检测关系类型（当上面的关系为HIT_RELATION = 3或者为BEYOND_THRESHOLD_HIT_RELATION = 5的时候）
    public static final int R_NONE = 0;                                         //无关系
    public static final int R_EQUAL = 1;                                        //等于
    public static final int R_NO_EQUAL = 2;                                     //不等于
    public static final int R_MORE = 3;                                         //大于
    public static final int R_MORE_EQUAL = 4;                                   //大于等于
    public static final int R_LESS = 5;                                         //小于
    public static final int R_LESS_EQUAL = 6;                                   //小于等于
    public static final int R_CONTAIN = 7;                                      //包含
    public static final int R_NOT_CONTAIN = 8;                                  //不包含
    public static final int R_BE_CONTAINED = 9;                                 //被包含（包含于）
    public static final int R_NOT_BE_CONTAINED = 10;                            //不被包含（不包含于）
    public static final int R_MATCH_REGEXP = 11;                                //匹配正则
    public static final int R_NOT_MATCH_REGEXP = 12;                            //不匹配正则

    public static final Map<Integer, String> RULE_EXPRESSION_MAP = new HashMap<Integer, String>(){
        {
            put(R_NONE, "无关系");
            put(R_EQUAL, "等于");
            put(R_NO_EQUAL, "不等于");
            put(R_MORE, "大于");
            put(R_MORE_EQUAL, "大于等于");
            put(R_LESS, "小于");
            put(R_LESS_EQUAL, "小于等于");
            put(R_CONTAIN, "包含");
            put(R_NOT_CONTAIN, "不包含");
            put(R_BE_CONTAINED, "被包含（包含于）");
            put(R_NOT_BE_CONTAINED, "不被包含（不包含于）");
            put(R_MATCH_REGEXP, "匹配正则");
            put(R_NOT_MATCH_REGEXP, "不匹配正则");
        }
    };

    //--------------------------------------------------------------------------意图状态类型
    public static final int GLOBAL_START_ID = 1;                                //全局开始状态
    public static final int GLOBAL_END_ID = 2;                                  //全局结束状态
    public static final int GLOBAL_ERROR_ID = 3;                                //全局错误状态
    public static final int GLOBAL_ANY_ID = 4;                                  //全局任意状态

    //--------------------------------------------------------------------------动作类型
    public static final int PROCESSED_ACTION = 1;                               //处理动作
    public static final int INTERACTIVE_ACTION = 2;                             //交互动作

    //--------------------------------------------------------------------------动作内部所使用到的数据类型
    public static final String BIZ_DATA = "bizData";                            //业务数据
    public static final String REPLY = "reply";                                 //必须存在的回复话术（这是唯一的一个放在框架的业务变量）

    //--------------------------------------------------------------------------一个参数保留的轮次
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOREVER = Integer.MAX_VALUE;

    //--------------------------------------------------------------------------逻辑运算符
    public static final int AND = 1;
    public static final int OR = 0;
    public static final int NOT = -1;
    public static final int XOR = -2;


}
