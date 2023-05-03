package com.cooler.ai.distribution.client.testcase;

import java.util.HashMap;
import java.util.Map;

public class BossHiring {
//    业务路由成功（标准问+业务）
    static String[] answers1 = {
            "您好，想跟您交流一下这个岗位。",
            "谢谢您！",
            ""
    };

    static String[] answers2 = {
            "您好，想跟您交流一下这个岗位。",
            "您好，简历已发，请您查收。",
            "谢谢您！",
            ""
    };




    public static Map<String, String[]> answersGroup = new HashMap<String, String[]>() {{
//        put("被拒绝表达谢谢", answers1);         //先设置resume_suitable = 0
        put("简历合适发出谢谢", answers2);

    }};

}
