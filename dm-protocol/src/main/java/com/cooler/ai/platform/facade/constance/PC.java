package com.cooler.ai.platform.facade.constance;

//此类定义了PARAM_VALUE_MAP包含的平台型参数（首尾为$）
public class PC {

    public static final String SENTENCE = "sentence";                               //原句

    public static final String NLU_DOMAIN = "last_nlu_domain";                      //上一次NLU领域名
    public static final String NLU_INTENT = "last_nlu_intent";                      //上一次NLU意图名
    public static final String SAME_DOMAIN = "same_domain";                         //没有换领域（上轮对话的领域和本轮对话领域是否一致）

    public static final String DOMAIN_NAME = "domain_name";                         //上一次领域名
    public static final String INTENT_NAME = "intent_name";                         //意图名称
    public static final String INTENT_ID = "intent_id";                             //意图id
    public static final String INTENT_TYPE = "intent_type";                         //意图类型

    public static final String TASK_ID = "task_id";                                 //任务ID
    public static final String TASK_NAME = "task_name";                             //任务名称
    public static final String TRANSFORM_INTENT_ID = "transform_intent_id";         //转义意图ID
    public static final String TRANSFORM_INTENT_NAME = "transform_intent_name";     //转义意图名称

    public static final String LAST_FROM_STATE_ID = "last_from_state_id";           //上一轮的起始状态
    public static final String FROM_STATE_ID = "from_state_id";                     //从哪个状态开始变迁（变迁之前的状态ID）
    public static final String FROM_STATE_ID2 = "from_state_id2";                   //FROM_STATE_ID变迁失败，会转到START_STATE_ID尝试，两个状态相同，则没有跳出上下文，不同，FROM_STATE_ID2必须为1，则跳出了上下文
    public static final String TO_STATE_ID = "to_state_id";                         //当前状态ID（变迁最后得到的状态ID）

    public static final String LAST_FROM_STATE = "last_from_state";                 //上一轮的起始状态
    public static final String FROM_STATE = "from_state";                           //从哪个状态开始变迁（变迁之前的状态ID）
    public static final String FROM_STATE2 = "from_state2";                         //FROM_STATE_ID变迁失败，会转到START_STATE_ID尝试，两个状态相同，则没有跳出上下文，不同，FROM_STATE_ID2必须为1，则跳出了上下文
    public static final String TO_STATE = "to_state";                               //当前状态ID（变迁最后得到的状态ID）


    public static final String TRANSFERRED_STEP_COUNT = "transferred_step_count";   //迁移步数
    public static final String LAST_SUCCESS_CONDITION_CONTEXT = "last_success_condition_context";  //最后成功迁移所使用的ConditionContext
    public static final String FIRST_BAD_CONDITION_DATA = "first_bad_condition_data";  //第一个导致变迁失败的变迁条件
    public static final String SELECTED_TRANSITIONS = "selected_transitions";       //选择的（多个）迁移

    public static final String POLICY_ID = "policy_id";                             //选择的策略ID

    public static final String REPLY = "reply";                                 //回复话术（这是唯一的一个放在框架的业务变量）

    public static final String[] PLATFORM_PARAMS = {                                //平台变量集
            SENTENCE, NLU_DOMAIN, NLU_INTENT, DOMAIN_NAME, SAME_DOMAIN, INTENT_ID, INTENT_NAME, INTENT_TYPE,
            TRANSFORM_INTENT_ID, TASK_ID, FROM_STATE_ID, FROM_STATE_ID2, TO_STATE_ID, TRANSFERRED_STEP_COUNT,
            LAST_SUCCESS_CONDITION_CONTEXT, FIRST_BAD_CONDITION_DATA, SELECTED_TRANSITIONS, POLICY_ID, REPLY
    };

}
