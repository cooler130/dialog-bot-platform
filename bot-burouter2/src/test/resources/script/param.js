
//ps_count的计算
function param_1(gps, lps){
        // var ps_count = "0";

        var ps_count = gps['%ps_count%'];
        if(!ps_count) ps_count = "0";
        ps_count = parseInt(ps_count);
        ps_count = ps_count + 1;
        gps['%ps_count%'] = ps_count + "";

        return {'gps': gps, 'lps': lps};
}

//tq_score的计算
function param_4(gps, lps){
        // var tqDataInfo = "{\"sentence\":\"我的红包怎么用不了啊？\",\"id\":\"tq-10008\",\"tq\":\"红包不生效\",\"belief\":0.96}";

        var tqDataInfo = lps['%TQDataInfo%'];
        var json = eval('(' + tqDataInfo + ')');
        var tq_score = json['belief'];
        if(tq_score > 0.9){
                gps['%fixedTqDataInfo%'] = tqDataInfo;
                gps['%fixedTqId%'] = json['id'];
        }
        var s_tq_score  = tq_score + '';
        lps["%tq_score%"] = s_tq_score;
        gps["%tq_score%"] = s_tq_score;

        return {'gps': gps, 'lps': lps};
}

//fixedTqId的获取或转换
function param_5(gps, lps){
        var fixedTqId = gps['%fixedTqId%'];
        if(!fixedTqId){
                fixedTqId = "none";
        }
        lps['%fixedTqId%'] = fixedTqId;
        return {'gps': gps, 'lps': lps};
}

//order_score的计算
function param_4(gps, lps){

        var orderDataInfo = lps['%OrderDataInfo%'];
        var json = eval('(' + orderDataInfo + ')');
        var order_score = json['belief'];
        if(order_score > 0.9){
                gps['%fixedOrderDataInfo%'] = orderDataInfo;
                gps['%fixedOrderId%'] = json['id'];
        }
        var s_order_score  = order_score + '';
        lps["%order_score%"] = s_order_score;
        gps["%order_score%"] = s_order_score;

        return {'gps': gps, 'lps': lps};
}


// var orderDataInfo = lps['%OrderDataInfo%'];
// var json = eval('(' + orderDataInfo + ')');
// var order_score = json['belief'];
// if(order_score > 0.9){
//         gps['%fixedOrderDataInfo%'] = fixedOrderDataInfo;
//         gps['%fixedOrderId%'] = json['id'];
// }
// var s_order_score  = order_score + '';
// lps["%order_score%"] = s_order_score;
// gps["%order_score%"] = s_order_score;

//bu_score的计算
function param_10(gps, lps){

        var buDataInfo = lps['%BuDataInfo%'];
        var json = eval('(' + buDataInfo + ')');
        var bu_score = json['belief'];
        if(bu_score > 0.9){
                gps['%fixedBuDataInfo%'] = buDataInfo;
                gps['%fixedBuId%'] = json['id'];
        }
        var s_bu_score  = bu_score + '';
        lps["%bu_score%"] = s_bu_score;
        gps["%bu_score%"] = s_bu_score;

        return {'gps': gps, 'lps': lps};
}

//order_count的计算
function param_11(gps, lps){
        // var order_count = "1";

        var order_count = gps['%order_count%'];
        if(!order_count) order_count = "0";
        order_count = parseInt(order_count);
        order_count = order_count + 1;
        gps['%order_count%'] = order_count + "";

        return {'gps': gps, 'lps': lps};
}

//bu_count的计算
function param_12(gps, lps){
        // var bu_count = "2";

        var bu_count = gps['%bu_count%'];
        if(!bu_count) bu_count = "0";
        bu_count = parseInt(bu_count);
        bu_count = bu_count + 1;
        gps['%bu_count%'] = bu_count + "";

        return {'gps': gps, 'lps': lps};
}

var gps = {
        '#client_name#': 'cooler_iphone',
        '#intent_name#': 'no_intent',
        '$last_nlu_domain$': 'burouter2',
        '$intent_type$': '1',
        '#session_id#': 'SID_TEST_1669730214955',
        '$from_state2$': 'global_start',
        '#task_name#': 'bu_route2',
        '$same_domain$': 'true',
        '#client_type#': 'h5',
        '$from_state$': 'global_start',
        '#channel#': '000',
        '$to_state$': 'global_start',
        '#client_id#': 'CID_8A831A1489014520BAB004F70C452396',
        '#city_name#': '北京',
        '#user_id#': 'TEST_USER_ID_1',
        '$policy_id$': '23',
        '$transform_intent_name$': 'no_intent',
        '#domain_name#': 'burouter2',
        '$domain_name$': 'burouter2',
        '#paramValueMap#': '{\'#signal#\':\'init\'}',
        '#user_name#': 'cooler',
        '#query_type#': 'signal',
        '$sentence$': 'signal类型数据',
        '$intent_id$': '301',
        '$last_from_state$': 'global_start',
        '#signal#': 'init',
        '$intent_name$': 'no_intent',
        '$task_name$': 'bu_route2',
        '#turn_num#': '1'
};
var lps = {};
var maps = param_11(gps, lps);
debugger;

