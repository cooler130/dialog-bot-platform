package com.cooler.ai.platform.client.model;


import com.cooler.ai.platform.facade.model.DomainInfo;
import com.cooler.ai.platform.facade.model.SlotInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2019/12/22
 **/

public class NluMockData {
    public static final Map<String, DomainInfo> nluMockMap = new HashMap<String, DomainInfo>() {
        {
            //testcase
//            put("我要点外卖",     createDomainInfo("我要点外卖", "waimai", "want_waimai", 1.0, null, null));
//            put("看一看肯德基",   createDomainInfo("看一看肯德基", "waimai", "reselect_restaurant", 0.85, new String[]{"restaurant_name"}, new String[]{"肯德基"}));
//            put("看一看麦当劳",   createDomainInfo("看一看麦当劳", "waimai", "reselect_restaurant", 0.85, new String[]{"restaurant_name"}, new String[]{"麦当劳"}));
//            put("返回商家",     createDomainInfo("返回商家", "waimai", "reselect_restaurant", 1.0, null, null));
//            put("我要吃套餐2",  createDomainInfo("我要吃套餐2", "waimai", "search", 1.0, new String[]{"dish_name"}, new String[]{"套餐2"}));
//            put("查看购物车",   createDomainInfo("查看购物车", "waimai", "check_shopping_cart", 1.0, null, null));
//            put("清空购物车",   createDomainInfo("清空购物车", "waimai", "clear_shopping_cart", 1.0, null, null));
//            put("再来一单",    createDomainInfo("再来一单", "waimai", "want_anotherorder", 1.0, null, null));
//            put("查询订单",     createDomainInfo("查询订单", "waimai", "inquire_order", 1.0, null, null));
//
//            put("帮助",          createDomainInfo("帮助", "no_domain", "help", 1.0, null, null));
//            put("按单价降序排列", createDomainInfo("按单价降序排列", "no_domain", "sort", 1.0, new String[]{"control_object", "sort_dim"}, new String[]{"price", "desc"}));
//            put("按单价升序排列", createDomainInfo("按单价升序排列", "no_domain", "sort", 1.0, new String[]{"control_object", "sort_dim"}, new String[]{"price", "asc"}));
//            put("第一个",       createDomainInfo("第一个", "no_domain", "select", 1.0, new String[]{"option_number"}, new String[]{"1"}));
//            put("第二个",       createDomainInfo("第二个", "no_domain", "select", 1.0, new String[]{"option_number"}, new String[]{"2"}));
//            put("第三个",       createDomainInfo("第三个", "no_domain", "select", 1.0, new String[]{"option_number"}, new String[]{"3"}));
//            put("换一批",      createDomainInfo("换一批", "no_domain", "control_want_more", 1.0, null, null));
//            put("下一页",      createDomainInfo("下一页", "no_domain", "control_want_more", 1.0, null, null));
//            put("重新开始",    createDomainInfo("重新开始", "no_domain", "restart", 1.0, null, null));
//            put("去下单",      createDomainInfo("去下单", "no_domain", "order_deal", 1.0, null, null));
//            put("支付",       createDomainInfo("支付", "no_domain", "pay", 1.0, null, null));
//            put("继续购物",    createDomainInfo("继续购物", "no_domain", "continue_ordering", 1.0, null, null));
//            put("哈哈",       createDomainInfo("哈哈", "no_domain", "no_intent", 1.0, null, null));
//


            //callout
            put("好的，你说",     createDomainInfo("好的，你说", "no_domain", "positive", 1.0, null, null));
            put("你说什么",     createDomainInfo("你说什么", "no_domain", "repeat", 1.0, null, null));
            put("喂喂",     createDomainInfo("喂喂", "no_domain", "ah", 1.0, null, null));

            put("",             createDomainInfo("", "no_domain", "no_intent", 1.0, null, null));
            put("用过",     createDomainInfo("用过", "no_domain", "unknown_intent", 1.0, null, null));
            put("外卖",     createDomainInfo("外卖", "no_domain", "unknown_intent", 1.0, null, null));
            put("还行吧",     createDomainInfo("还行吧", "no_domain", "unknown_intent", 1.0, null, null));
            put("不想透露",     createDomainInfo("不想透露", "no_domain", "unknown_intent", 1.0, null, null));
            put("嗯，再见",     createDomainInfo("嗯，再见", "no_domain", "unknown_intent", 1.0, null, null));
            put("哈哈",          createDomainInfo("哈哈", "no_domain", "unknown_intent", 0.0, null, null));

            put("没兴趣",     createDomainInfo("没兴趣", "no_domain", "negative", 1.0, null, null));
            put("不行",           createDomainInfo("不行", "no_domain", "refuse", 1.0, null, null));
            put("停止",           createDomainInfo("停止", "no_domain", "refuse", 1.0, null, null));
            put("我现在很忙",     createDomainInfo("我现在很忙", "no_domain", "busy", 1.0, null, null));
            put("我在开车",     createDomainInfo("我在开车", "no_domain", "driving", 1.0, null, null));
            put("我在哄小孩",     createDomainInfo("我在哄小孩", "no_domain", "play_with_baby", 1.0, null, null));

            put("你多大了",     createDomainInfo("你多大了", "no_domain", "how_old", 1.0, null, null));


            //burouter
            put("我的红包怎么用不了啊？",     createDomainInfo("我的红包怎么用不了啊？", "burouter", "express_question", 1.0, null, null));
            put("我的红包呢？",             createDomainInfo("我的红包呢？",         "burouter", "unknown_intent", 1.0, null, null));

            put("不是",                    createDomainInfo("不是",                "no_domain", "negative", 1.0, null, null));
            put("都不是",                  createDomainInfo("都不是",                "no_domain", "negative", 1.0, null, null));
            put("是的",                    createDomainInfo("是的",                "no_domain", "positive", 1.0, null, null));
            put("电影",                    createDomainInfo("电影",                "no_domain", "express_business", 1.0, null, null));
            put("第二个",                  createDomainInfo("第二个",                "no_domain", "select", 1.0, new String[]{"option_number"}, new String[]{"2"}));
            put("转人工",                  createDomainInfo("转人工",                "no_domain", "people_service", 1.0, null, null));

        }
    };

    /**
     * 创建NLUData下面的DomainInfo
     * @param query
     * @param domainName
     * @param intentName
     * @param score
     * @param slotNames
     * @param slotValues
     * @return
     */
    private static DomainInfo createDomainInfo(String query, String domainName, String intentName, double score, String[] slotNames, String[] slotValues){
        DomainInfo domainInfo = new DomainInfo();
        domainInfo.setDomainName(domainName);
        domainInfo.setIntentName(intentName);
        domainInfo.setScore(score);
        domainInfo.setUtterance(query);
        domainInfo.setUttSegment("");

        Map<Integer, SlotInfo> slotInfoMap = new HashMap<>();
        if(slotNames != null){
            int slotSize = slotNames.length;
            for (int i = 0; i < slotSize; i++) {
                SlotInfo slotInfo = new SlotInfo();
                slotInfo.setIndex(i);
                slotInfo.setName(slotNames[i]);
                slotInfo.setValue(slotValues[i]);
                slotInfo.setOriginalText(slotValues[i]);
                slotInfoMap.put(i, slotInfo);
            }
        }
        domainInfo.setSlots(slotInfoMap);
        return domainInfo;
    }
        
        
}
