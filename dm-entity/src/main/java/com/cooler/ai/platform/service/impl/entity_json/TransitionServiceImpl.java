package com.cooler.ai.platform.service.impl.entity_json;

import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.platform.entity.Transition;
import com.cooler.ai.platform.model.EntityConstant;
import com.cooler.ai.platform.service.entity.TransitionService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("jsonTransitionService")
public class TransitionServiceImpl implements TransitionService{

    private static Map<String, String> globalMap = EntityConstant.globalMap;

    @Override
    public List<Transition> selectByTaskStartStateId(String taskName, Integer startStateId, String intentName) {
        List<Transition> transitions = null;
        String json = globalMap.get("Transition_" + taskName + "_" + startStateId);
        if(json != null){
            List<Transition> filterTransitions = new ArrayList<>();
            transitions = JSONObject.parseArray(json, Transition.class);
            for (Transition transition : transitions) {                                                                     //过滤掉那些不包含给定intentName的Transition集合
                String relatedIntentNames = transition.getRelatedIntentNames();
                String[] relatedIntents = relatedIntentNames.split(",");
                Set<String> relatedIntentSet = new HashSet<>(Arrays.asList(relatedIntents));

                if (relatedIntentSet.contains(EntityConstant.ANY_INTENT)){                                                  //特殊情况，相关意图集包含EntityConstant.ANY_INTENT（any_intent），则算是检测黑名单
                    Set blackRelatedIntentSet = new HashSet();
                    for (String relatedIntent : relatedIntentSet) {
                        if(relatedIntent.startsWith("!")){
                            String blackRelatedIntent = relatedIntent.replace("!", "").trim();                //删除感叹号，感叹号表示排除这个intentName
                            blackRelatedIntentSet.add(blackRelatedIntent);
                        }
                    }
                    blackRelatedIntentSet.remove(EntityConstant.ANY_INTENT);                                                //删除EntityConstant.ANY_INTENT本身， EntityConstant.ANY_INTENT起一个标示黑名单的作用
                    if(!blackRelatedIntentSet.contains(intentName)){
                        filterTransitions.add(transition);
                    }
                }else{                                                                                                      //一般情况，相关意图集不包含EntityConstant.ANY_INTENT（any_intent），则算是检测白名单
                    if(relatedIntentSet.contains(intentName)){
                        filterTransitions.add(transition);
                    }
                }
            }
            return filterTransitions;
        }
        return null;
    }

    @Override
    public List<Transition> selectByTaskName(String taskName) {
        List<Transition> transitions = null;
        String json = globalMap.get("Transition_" + taskName);
        if(json != null){
            transitions = JSONObject.parseArray(json, Transition.class);
        }
        return transitions;
    }
}
