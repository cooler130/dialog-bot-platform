
//ps_count的计算
function param_1(bps, lps){
        // var ps_count = "0";

        var ps_count = bps['%ps_count%'];
        if(!ps_count) ps_count = "0";
        ps_count = parseInt(ps_count);
        ps_count = ps_count + 1;
        bps['%ps_count%'] = ps_count + "";

        return {'bps': bps, 'lps': lps};
}

//tq_score的计算
function param_4(bps, lps){
        // var tqDataInfo = "{\"sentence\":\"我的红包怎么用不了啊？\",\"id\":\"tq-10008\",\"tq\":\"红包不生效\",\"belief\":0.96}";

        var tqDataInfo = lps['%TQDataInfo%'];
        var json = eval('(' + tqDataInfo + ')');
        var tq_score = json['belief'];
        if(tq_score > 0.9){
                bps['%fixedTqDataInfo%'] = tqDataInfo;
                bps['%fixedTqId%'] = json['id'];
        }
        var s_tq_score  = tq_score + '';
        lps["%tq_score%"] = s_tq_score;
        bps["%tq_score%"] = s_tq_score;

        return {'bps': bps, 'lps': lps};
}

//fixedTqId的获取或转换
function param_5(bps, lps){
        var fixedTqId = bps['%fixedTqId%'];
        if(!fixedTqId){
                fixedTqId = "none";
        }
        lps['%fixedTqId%'] = fixedTqId;
        return {'bps': bps, 'lps': lps};
}

//order_score的计算
function param_4(bps, lps){

        var orderDataInfo = lps['%OrderDataInfo%'];
        var json = eval('(' + orderDataInfo + ')');
        var order_score = json['belief'];
        if(order_score > 0.9){
                bps['%fixedOrderDataInfo%'] = orderDataInfo;
                bps['%fixedOrderId%'] = json['id'];
        }
        var s_order_score  = order_score + '';
        lps["%order_score%"] = s_order_score;
        bps["%order_score%"] = s_order_score;

        return {'bps': bps, 'lps': lps};
}


// var orderDataInfo = lps['%OrderDataInfo%'];
// var json = eval('(' + orderDataInfo + ')');
// var order_score = json['belief'];
// if(order_score > 0.9){
//         bps['%fixedOrderDataInfo%'] = fixedOrderDataInfo;
//         bps['%fixedOrderId%'] = json['id'];
// }
// var s_order_score  = order_score + '';
// lps["%order_score%"] = s_order_score;
// bps["%order_score%"] = s_order_score;

//bu_score的计算
function param_10(bps, lps){
        // var buDataInfosJS = "[{\"belief\":9.5,\"bu\":\"高端餐饮（from order）\",\"id\":\"bu-001\"}]";
        // var s_option_num = "0";

        var buDataInfosJS = lps['%BuDataInfos%'];
        var s_option_num = sps["@option_num@"];
        if(!buDataInfosJS) return;
        if(!s_option_num) s_option_num = "0";
        var option_num = parseInt(s_option_num);
        var buDataInfos = eval('(' + buDataInfosJS + ')');
        var buDataInfo;
        if(buDataInfos.length > option_num){
                buDataInfo = buDataInfos[option_num];
        }else{
                buDataInfo = buDataInfos[0];
        }
        var bu_score = buDataInfo['belief'];
        if(bu_score > 0.9){
                bps['%fixedBuDataInfo%'] = JSON.stringify(buDataInfo);
                bps['%fixedBuId%'] = buDataInfo['id'];
        }
        var s_bu_score  = bu_score + '';
        lps["%bu_score%"] = s_bu_score;
        bps["%bu_score%"] = s_bu_score;

        return {'bps': bps, 'lps': lps};
}

//order_count的计算
function param_11(bps, lps){
        // var order_count = "1";

        var order_count = bps['%order_count%'];
        if(!order_count) order_count = "0";
        order_count = parseInt(order_count);
        order_count = order_count + 1;
        bps['%order_count%'] = order_count + "";

        return {'bps': bps, 'lps': lps};
}

//bu_count的计算
function param_12(bps, lps){
        // var bu_count = "2";

        var bu_count = bps['%bu_count%'];
        if(!bu_count) bu_count = "0";
        bu_count = parseInt(bu_count);
        bu_count = bu_count + 1;
        bps['%bu_count%'] = bu_count + "";

        return {'bps': bps, 'lps': lps};
}

var bps = {};
var lps = {};
var maps = param_10(bps, lps);
debugger;

