package com.cooler.ai.platform.facade.constance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//此类定义了PARAM_VALUE_MAP包含的平台型参数（首尾为$）
public class PC {

    public static final String SENTENCE = "sentence";                               //原句

    public static final String NLU_DOMAIN_NAME = "nlu_domain_name";                 //NLU领域名（由NLU解析得到）
    public static final String NLU_INTENT_NAME = "nlu_intent_name";                 //NLU意图名（由NLU解析得到）

    public static final String SAME_DOMAIN = "same_domain";                         //没有换领域（上轮对话的领域和本轮对话领域是否一致）


    public static final String DOMAIN_NAME = "domain_name";                         //DM领域名称（由DC计算得到）
    public static final String TASK_NAME = "task_name";                             //任务名称（由DC计算得到）
    public static final String INTENT_NAME = "intent_name";                         //DM意图名称（由DC计算得到）
    public static final String INTENT_ID = "intent_id";                             //DM意图id
    public static final String INTENT_TYPE = "intent_type";                         //DM意图类型
    public static final String TRANSFORM_INTENT_NAME = "transform_intent_name";     //DM转义意图名称

    public static final String CHECKED_CONDITIONS = "checked_conditions";           //检验后的条件集合

    public static final String LAST_FROM_STATE = "last_from_state";                 //上一轮的起始状态
    public static final String FROM_STATE = "from_state";                           //从哪个状态开始变迁（变迁之前的状态ID）
    public static final String FROM_STATE2 = "from_state2";                         //FROM_STATE_ID变迁失败，会转到START_STATE_ID尝试，两个状态相同，则没有跳出上下文，不同，FROM_STATE_ID2必须为1，则跳出了上下文
    public static final String TO_STATE = "to_state";                               //当前状态ID（变迁最后得到的状态ID）

    public static final String POLICY_ID = "policy_id";                             //选择的策略ID


    public static final Set<String> PC_PARAM_SET = new HashSet<>(
        Arrays.asList(
                SENTENCE, NLU_DOMAIN_NAME, NLU_INTENT_NAME, SAME_DOMAIN, DOMAIN_NAME, TASK_NAME, INTENT_NAME, INTENT_ID, INTENT_TYPE,
        TRANSFORM_INTENT_NAME, CHECKED_CONDITIONS, LAST_FROM_STATE, FROM_STATE, FROM_STATE2, TO_STATE, POLICY_ID)
    );

}
