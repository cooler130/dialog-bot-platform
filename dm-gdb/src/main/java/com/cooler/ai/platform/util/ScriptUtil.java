package com.cooler.ai.platform.util;

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
     * @param gps
     * @param lps
     * @return  必须为Map<String, Map<String, String>>
     */
    public static Map<String, Map<String, String>> runScript(String script, Map<String, String> gps, Map<String, String> lps) {
        if(script == null || script.length() == 0) return null;
        if (engine instanceof Invocable) {
            try {
                script = "function run(gps, lps) {"
                        + script
                        + "return {'gps':gps,'lps':lps};}";
                engine.eval(script);
                Invocable invokeEngine = (Invocable) engine;
                Object o = invokeEngine.invokeFunction("run", gps, lps);
                ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror)o;
                Map<String, Map<String, String>> twoMap = new HashMap<>();
                twoMap.put("gps", (Map<String, String>)scriptObjectMirror.get("gps"));
                twoMap.put("lps", (Map<String, String>)scriptObjectMirror.get("lps"));
                return twoMap;
            } catch (NoSuchMethodException e) {
                logger.error("方法无法找到: ",  e);
            } catch (ScriptException e) {
                logger.error("脚本错误: ",  e);
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