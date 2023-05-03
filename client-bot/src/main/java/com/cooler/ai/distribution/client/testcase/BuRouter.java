package com.cooler.ai.distribution.client.testcase;

import java.util.HashMap;
import java.util.Map;

public class BuRouter {
//    业务路由成功（标准问+业务）
    static String[] answers1 = {
            "我的红包怎么用不了啊？",
            "不是",
            "不是",
            "都不是",
            "电影",
            ""
    };

//    业务路由成功（标准问+业务选择）
    static String[] answers2 = {
            "我的红包怎么用不了啊？",
            "不是",
            "不是",
            "都不是",
            "第二个",
            ""
    };

    static String[] answers2a = {
            "我的红包怎么用不了啊？",
            "不是",
            "不是",
            "都不是",
            "都不是",
            "都不是",
            ""
    };


    static String[] answers3 = {
            "我的红包怎么用不了啊？",
            "是的",
            ""
    };
    static String[] answers4 = {
            "我的红包怎么用不了啊？",
            "不是",
            "是的",
            ""
    };

    static String[] answers4a = {
            "我的红包怎么用不了啊？",
            "不是",
//            "卡通",
            ""
    };

    static String[] answers5 = {
            "转人工",
            "转人工",
            ""
    };

    //标准问都设为低于0.9
    static String[] answers6 = {
            "我的红包怎么用不了啊？",
            "不是",
            "不是",
            ""
    };

    static String[] answers7 = {
            "我的红包怎么用不了啊？",
            "不是",
            "我说的是我昨天的外卖订单。",
            "是的",
            ""
    };


    public static Map<String, String[]> answersGroup = new HashMap<String, String[]>() {{
        put("业务路由成功（标准问+业务）", answers1);
        put("业务路由成功（标准问+业务选择）", answers2);
        put("业务路由成功（标准问）", answers2a);
        put("业务路由成功（标准问+订单1）", answers3);
        put("业务路由成功（标准问+订单2）", answers4);
        put("业务路由成功（标准问+订单1+无意图）", answers4a);
        put("转人工两次", answers5);
        put("业务路由失败（标准问错2次）", answers6);
        put("订单路由失败（标准问错2次）", answers7);
    }};

}
