package com.cooler.ai.dm.taskaction.process;

import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.BizDataModelState;
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
@Component("getRepeatScriptTaskAction")
public class GetRepeatScriptTaskAction extends BaseInnerProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(GetRepeatScriptTaskAction.class);

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("GetRepeatScriptTaskAction  （处理动作）重复请求");

        //1.（准备Intent值）     获取当前识别到的Intent

        //2.设计好传出的数据
        String repeatScript = null;
        String guideOptionsJS = null;
        String nlgTemplateInfoJS = null;
        String repeatCountStr = null;

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        String lastReply = getBizDataValue(Constant.REPLY);                                                     //reply是一个特殊的业务变量
        repeatScript = lastReply;

        String lastNlgTemplateInfoJS = getBizDataValue(BC.NLG_TEMPLATE_INFO_JS);
        nlgTemplateInfoJS = lastNlgTemplateInfoJS;

        String lastGuideOptionsJS = getBizDataValue(BC.GUIDE_OPTIONS_JS);
        guideOptionsJS = lastGuideOptionsJS;

        repeatCountStr = getBizDataValueOrDefault(BC.REPEAT_COUNT, "0");
        int repeatCount = Integer.parseInt(repeatCountStr) + 1;

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addToBizDataMSMap(bizDataMapTmp, BC.SCRIPT, repeatScript, Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.NLG_TEMPLATE_INFO_JS, nlgTemplateInfoJS, Constant.ONE);
        addToBizDataMSMap(bizDataMapTmp, BC.GUIDE_OPTIONS_JS, guideOptionsJS, Constant.ONE);
        addToBizDataMSMap(bizDataMapTmp, BC.REPEAT_COUNT, repeatCount + "", Constant.ONE);                                   //这个记忆1轮

        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "reportScript";
    }
}
