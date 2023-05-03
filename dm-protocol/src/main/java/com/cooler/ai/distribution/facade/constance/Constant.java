package com.cooler.ai.distribution.facade.constance;

import java.util.regex.Pattern;

public class Constant {

    public static final int MAX_BACKUP_TURN_COUNT = 5;                          //最多回溯多少轮对话
    public static final int MAX_DOMAIN_DATA_COUNT = 1;                          //最多获取多少个领域业务数据

    public static final String MODEL_RDB = "model_rdb";                         //关系型数据库模式（使用mysql，通常用于调试）
    public static final String MODEL_KVDB = "model_json";                       //json数据模式（通常用于线上，使用json文件，其是在关系数据库基础上生成的json文件）
    public static final String MODEL_GDB = "model_gdb";                         //图数据库模式（当前使用neo4j）
    public static final String MODEL_TREE = "model_tree";                       //树数据模式（当前使用xmind）

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
    public static final String SLOT_STATE_MAP = "slotStateMap";                         //槽位状态集标示（从NLU结果解析获取）
    public static final String UNKNOWN_SLOT_STATE_MAP = "unknownSlotStateMap";          //未识别槽位状态集标示（从NLU结果解析获取）

    public static final String PLATFORM_PARAM_MAP = "platformParamMap";                 //平台参数Map（系统参数Map）
    public static final String SLOT_PARAM_MAP = "slotParamMap";                         //槽位参数Map
    public static final String CUSTOM_PARAM_MAP = "customParamMap";                     //用户定制化参数Map
    public static final String BIZ_PARAM_MAP = "bizParamMap";                           //业务参数Map

    public static final String SO_DOMAIN_DECISION_MAP = "soDomainDecisionMap";          //（SlotOperate阶段记录）记录在多domain进行的决策过程种产生的决策数据

    public static final String DOMAIN_DATAS = "domainDatas";                            //槽位操作记录（历史槽值集合、本轮得到的槽值集合、替换记录、最终确认的槽位集合）
    public static final String DOMAIN_INDICATORS = "domainIndicators";                  //领域决策过程记录（各个领域所获取的比较特征数据，以及比较数据集合）

    //--------------------------------------------------------------------------参数类型

    public static final String UNKNOWN_PARAM = "unknown";                               //未知类型参数
    public static final String SLOT_PARAM = "slot";                                     //槽位参数
    public static final String CUSTOM_PARAM = "custom";                                 //定制/用户参数（首尾有#，包含用户请求数据、用户定制化数据）
    public static final String PLATFORM_PARAM = "platform";                             //平台参数（首尾有$，有限的几个：sameDomain、currentStateId、dmDomain、nluDomain、intentId、intentName）
    public static final String BIZ_PARAM = "biz";                                       //业务参数（首尾有%，由以上参数调接口得来，跟业务相关）

    //--------------------------------------------------------------------------意图状态类型
    public static final String GLOBAL_START = "global_start";
    public static final String GLOBAL_END = "global_end";
    public static final String GLOBAL_ERROR = "global_error";
    public static final String GLOBAL_ANY = "global_any";

    //--------------------------------------------------------------------------动作类型
    public static final int PROCESSED_ACTION = 1;                                       //处理动作
    public static final int INTERACTIVE_ACTION = 2;                                     //交互动作
    public static final int HTTP_ACTION = 3;                                            //http调用动作

    //--------------------------------------------------------------------------变量获取方式
    public static final int SCRIPT_ACQUIRE = 1;                                         //脚本计算获取
    public static final int HTTP_ACQUIRE = 2;                                           //http调用获取

    public static final Pattern systemParamPattern = Pattern.compile("\\$[a-zA-Z0-9\\_\\-]+\\$");                   //系统变量模板
    public static final Pattern customerParamPattern = Pattern.compile("\\#[a-zA-Z0-9\\_\\-]+\\#");                 //定制化变量模板
    public static final Pattern businessParamPattern = Pattern.compile("\\%[a-zA-Z0-9\\_\\-]+\\%");                 //业务变量模板
    public static final Pattern slotParamPattern = Pattern.compile("\\@[a-zA-Z0-9\\_\\-]+\\@");                     //槽位变量模板


    //--------------------------------------------------------------------------动作内部所使用到的数据类型
    public static final String BIZ_DATA = "bizData";                                    //业务数据

}
