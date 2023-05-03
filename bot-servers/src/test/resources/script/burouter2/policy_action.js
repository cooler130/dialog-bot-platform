
function run_1(gps, lps){
    gps['%about_order%']=1;
    return {'gps': gps, 'lps': lps};
}

//ps_count的计算
function run_2(gps, lps){
        var ps_count = gps['%ps_count%'];
        if(!ps_count) ps_count = 0;
        ps_count = ps_count + 1;
        gps['%ps_count%'] = ps_count + "";
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
var maps = run_2(gps, lps);
debugger;

