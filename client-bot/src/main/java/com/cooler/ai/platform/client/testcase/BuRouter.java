package com.cooler.ai.platform.client.testcase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangsheng on 2018/7/17.
 */
public class BuRouter {
//    业务路由成功（标准问+业务）
    static String[] querys1 = {
            "我的红包怎么用不了啊？",
            "不是",
            "不是",
            "都不是",
            "电影",
            ""
    };

//    业务路由成功（标准问+业务选择）
    static String[] querys2 = {
            "我的红包怎么用不了啊？",
            "不是",
            "不是",
            "都不是",
            "第二个",
            ""
    };

    static String[] querys2a = {
            "我的红包怎么用不了啊？",
            "不是",
            "不是",
            "都不是",
            "都不是",
            ""
    };


    static String[] querys3 = {
            "我的红包怎么用不了啊？",
            "是的",
            ""
    };
    static String[] querys4 = {
            "我的红包怎么用不了啊？",
            "不是",
            "是的",
            ""
    };

    static String[] querys5 = {
            "转人工",
            "转人工",
            ""
    };

    //标准问都设为低于0.9
    static String[] querys6 = {
            "我的红包怎么用不了啊？",
            "不是",
            "不是",
            ""
    };


    public static Map<String, String[]> querysGroup = new HashMap<String, String[]>() {{
        put("业务路由成功（标准问+业务）", querys1);
        put("业务路由成功（标准问+业务选择）", querys2);
        put("业务路由成功（标准问）", querys2a);
        put("业务路由成功（标准问+订单1）", querys3);
        put("业务路由成功（标准问+订单2）", querys4);
        put("转人工两次", querys5);
//        put("业务路由失败（标准问错2次）", querys6);
    }};

}
