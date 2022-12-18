package com.cooler.ai.platform.util;

import com.cooler.ai.platform.facade.constance.Constant;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static final Pattern[] patterns = {
            Constant.customerParamPattern,
            Constant.systemParamPattern,
            Constant.businessParamPattern,
            Constant.slotParamPattern
    };

    /**
     * 将模板中的不同类别的变量（系统变量、业务变量、用户变量、槽位变量）替代为具体值
     * @param template      可能包含各类变量的模板语句
     * @param localParams   局部变量（来源于脚本计算和输出）
     * @param platformParams
     * @param customParams
     * @param slotParams
     * @param bizParams
     * @return
     */
    public static String replaceVariableValues(String template,
                                               Map<String, String> platformParams, Map<String, String> customParams,
                                               Map<String, String> slotParams, Map<String, String> bizParams,
                                               Map<String, String> localParams){
        if(template == null || template.length() == 0) return template;
        for (Pattern pattern : patterns) {
            if((pattern == Constant.customerParamPattern && !template.contains("#"))
                    || (pattern == Constant.systemParamPattern && !template.contains("$"))
                    || (pattern == Constant.businessParamPattern && !template.contains("%"))
                    || (pattern == Constant.slotParamPattern && !template.contains("@"))
            ) continue;

            Matcher matcher = pattern.matcher(template);
            while (matcher.find()) {
                String paramName = matcher.group(0);

                String paramValue = localParams.get(paramName);                                                         //先替代局部变量值
                if(paramValue != null){
                    template = template.replace(paramName, paramValue);
                }else{
                    Map<String, String> params = null;
                    if(paramName.startsWith("$")){
                        params = platformParams;
                    }else if(paramName.startsWith("#")){
                        params = customParams;
                    }else if(paramName.startsWith("@")){
                        params = slotParams;
                    }else if(paramName.startsWith("%")){
                        params = bizParams;
                    }

                    paramValue = params.get(paramName);                                                           //再替代全局变量值
                    if(paramValue != null) {
                        template = template.replace(paramName, paramValue);
                    }else{
                        template = template.replace(paramName, "");                                          //如果两个Map都没找到，也要用过""替代里面的变量（不能留下变量）

                    }
                }
            }
        }
        return template;
    }

}
