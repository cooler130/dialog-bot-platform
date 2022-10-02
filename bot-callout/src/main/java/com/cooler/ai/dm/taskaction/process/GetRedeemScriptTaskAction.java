package com.cooler.ai.dm.taskaction.process;

import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.nlg.NlgConstant;
import com.cooler.ai.nlg.entity.NlgGuidesInfo;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.model.EntityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("getRedeemScriptTaskAction")
public class GetRedeemScriptTaskAction extends BaseInnerProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(GetRedeemScriptTaskAction.class);

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("GetRedeemScriptTaskAction  （处理动作）获取挽回话术");

        //1.（准备Intent值）     获取当前识别到的Intent

        //2.设计好传出的数据
        String redeemScript = null;
        String redeemCountStr = getBizDataValueOrDefault(BC.REDEEM_COUNT, "0");
        int redeemCount = Integer.parseInt(redeemCountStr);

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        NlgGuidesInfo nlgGuidesInfo = getNlgGuidesInfoOfDefaultTheme(this.getClass(), NlgConstant.V_NONE_VALUE, NlgConstant.V_NONE_VALUE, null);
        redeemCount ++;

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addNlgGuidesInfoToMap(bizDataMapTmp, nlgGuidesInfo, Constant.ONE);

        addToBizDataMSMap(bizDataMapTmp, BC.REDEEM_COUNT, redeemCount + "", Constant.FOREVER);
        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "reportScript";
    }
}
