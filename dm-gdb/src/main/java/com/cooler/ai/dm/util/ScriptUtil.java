package com.cooler.ai.dm.util;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.util.HashMap;
import java.util.Map;

public class ScriptUtil {

    private static Logger logger =  LoggerFactory.getLogger(ScriptUtil.class);
    private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");

    /**
     * 执行规定了格式的脚本
     * @param script
     * @param pps   platform params
     * @param cps   custom params
     * @param sps   slot params
     * @param bps   biz params
     * @param lps   local params
     * @return  必须为Map<String, Map<String, String>>，只传出来bps和lps，前面三个只读不写
     */
    public static Map<String, Map<String, String>> runScript(String script,
                                                             Map<String, String> pps,
                                                             Map<String, String> cps,
                                                             Map<String, String> sps,
                                                             Map<String, String> bps,
                                                             Map<String, String> lps) {
        if(script == null || script.length() == 0) return null;
        if (engine instanceof Invocable) {
            try {
                script = "function run(pps, cps, sps, bps, lps) { "
                        + script
                        + "return {'bps':bps,'lps':lps};} ";
                engine.eval(script);
                Invocable invokeEngine = (Invocable) engine;
                Object o = invokeEngine.invokeFunction("run", pps, cps, sps, bps, lps);
                Map<String, Map<String, String>> newTwoMap = null;
                if(o != null){
                    ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror)o;
                    newTwoMap = new HashMap<>();
                    newTwoMap.put("bps", (Map<String, String>)scriptObjectMirror.get("bps"));
                    newTwoMap.put("lps", (Map<String, String>)scriptObjectMirror.get("lps"));
                }
                return newTwoMap;
            } catch (NoSuchMethodException e) {
                logger.error("方法无法找到: ",  e);
            } catch (ScriptException e) {
                logger.error("脚本错误: ",  e);
                logger.error(script);
            }
        } else {
            logger.error("不能支持此脚本");
        }
        return null;
    }

//    public static void main(String[] args) {
//        Map<String, String> gps = new HashMap<>();
//        gps.put("#name#", "apple");
//        gps.put("age", "18");
//        Map<String, String> lps = new HashMap<>();
//        lps.put("#sex#", "男");
//        lps.put("age", "30");
//        String script =
//                "var name = gps['#name#'];" +
//                "var subName = name.substring(0,3);" +
//                "subName = name.substring(1,3);" +
//                "gps.name = subName;" +
//                "gps.age = '19';" +
//                "if(gps.age > 15) gps['#age1#'] = 'old';" +
//                "lps['#sex#'] = '女';";
//
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 1000000; i++) {
//
//            Map<String, Map<String, String>> stringMapMap = ScriptUtil.runScript(script, gps, lps);
//
//        }
//
//
//        long end = System.currentTimeMillis();
//        System.out.println(end - start);
//
//    }
}