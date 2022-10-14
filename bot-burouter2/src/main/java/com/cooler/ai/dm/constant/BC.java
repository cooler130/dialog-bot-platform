package com.cooler.ai.dm.constant;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2019/10/14
 **/

/**
 * 此类定义了所有系统的业务数据，可以跟SC里面的数据有交集，业务数据两类用途：1.对外展示 2.参与下轮对话的计算过程
 */
public class BC {

    //1.得到的标准问、订单、业务数据（都是集合）
    public static final String TQ_DATA = "tq_data";

    public static final String ORDER_DATA = "order_data";

    public static final String BU_DATA = "bu_data";

    //2.确定的标准问、订单、业务数据
    public static final String FIXED_TQ_DATA = "fixed_tq_data";

    public static final String FIXED_ORDER_DATA = "fixed_order_data";

    public static final String FIXED_BU_DATA = "fixed_bu_data";


    //标准问、订单、业务执行度
    public static final String TQ_BELIEF = "tq_belief";

    public static final String ORDER_BELIEF = "order_belief";

    public static final String BU_BELIEF = "bu_belief";


    //各类过程数据
    public static final String TQ_COUNT = "tq_count";

    public static final String ORDER_COUNT = "order_count";

    public static final String BU_COUNT = "bu_count";

    public static final String PS_COUNT = "people_service_count";

    public static final String IS_ABOUT_ORDER = "is_about_order";


    public static final String INTENT_POSITIVE = "positive";        //肯定意图

    public static final String INTENT_NEGATIVE = "negative";        //否定意图

}
