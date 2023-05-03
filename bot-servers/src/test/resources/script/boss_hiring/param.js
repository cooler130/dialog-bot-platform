
//ask_other_count的计算
function param_2(bps, lps){
        // var ps_count = "0";

        var ask_other_count = bps['%ask_other_count%'];
        if(!ask_other_count) ask_other_count = "0";
        ask_other_count = parseInt(ask_other_count);
        ask_other_count = ask_other_count + 1;
        bps['%ask_other_count%'] = ask_other_count + "";

        return {'bps': bps, 'lps': lps};
}

//give_resume_count的计算
function param_4(bps, lps){

        var give_resume_count = bps['%give_resume_count%'];
        if(!give_resume_count) give_resume_count = "0";
        give_resume_count = parseInt(give_resume_count);
        give_resume_count = give_resume_count + 1;
        bps['%give_resume_count%'] = give_resume_count + "";

        return {'bps': bps, 'lps': lps};
}

//resume_suitable 的计算
function param_6(bps, lps){

        bps['%resume_suitable%'] = true;

        return {'bps': bps, 'lps': lps};
}


var bps = {};
var lps = {};
var maps = param_2(bps, lps);
debugger;

