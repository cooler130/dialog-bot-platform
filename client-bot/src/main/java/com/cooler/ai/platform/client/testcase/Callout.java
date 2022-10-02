package com.cooler.ai.platform.client.testcase;

import java.util.*;

/**
 * Created by zhangsheng on 2018/7/17.
 */
public class Callout {

    private static final String timeoutQuery = "signal->callout|no_intent|surveys|#signal#:timeout";

    static String[] answers1 = {
            "好的，你说",
            "用过",
            "外卖",
            "你多大了",
            "还行吧",
            "嗯，再见"
    };

    static String[] answers2 = {
            "我现在很忙",
            "哈哈",
            "停止",
            "不想透露",
            "嗯，再见"
    };

    static String[] answers3 = {
            "没兴趣",                              //此处"没兴趣"作为unknown_intent处理
            "没兴趣",
            "嗯，再见"
    };

    static String[] answers4 = {
            "喂喂",
            "你说什么",
            "你说什么",
            "用过",
            "你说什么",
            "你说什么",
            "你说什么",
            "外卖",
            "你说什么",
            "还行吧",
            ""
    };

    static String[] answers5 = {
            "好的，你说",
            "用过",
            timeoutQuery,
            timeoutQuery,
            "外卖",
            timeoutQuery,
            timeoutQuery,
            timeoutQuery,
            ""
    };

    static String[] answers6 = {
            "没兴趣",                              //此处"没兴趣"作为unknown_intent处理
            "你多大了",
            "我在开车",                             //这里也可以用，"我在哄小孩",已经做好配置，这个case体现了流程对新意图的扩展性
            "嗯，再见"
    };

    public static Map<String, String[]> querysGroup = new HashMap<String, String[]>() {{
        put("正常流程", answers1);
        put("拒绝流程", answers2);
//        put("两次拒绝意图", answers3);                        //todo：这个还是没通过
        put("连续重复", answers4);
        put("超时", answers5);
        put("拒绝+开车（转义能力）", answers6);
    }};

}
