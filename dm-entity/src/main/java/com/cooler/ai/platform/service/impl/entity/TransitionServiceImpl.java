package com.cooler.ai.platform.service.impl.entity;

import com.cooler.ai.platform.dao.TransitionMapper;
import com.cooler.ai.platform.entity.Transition;
import com.cooler.ai.platform.model.EntityConstant;
import com.cooler.ai.platform.service.entity.TransitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("transitionService")
public class TransitionServiceImpl implements TransitionService{

    @Autowired
    private TransitionMapper transitionMapper;

    @Override
    public List<Transition> selectByTaskStartStateId(String taskName, Integer currentStateId, String intentName) {
        List<Transition> filterTransitions = new ArrayList<>();
        List<Transition> transitions = transitionMapper.selectByTaskStartStateId(taskName, currentStateId);
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

    @Override
    public List<Transition> selectByTaskName(String taskName) {
        return transitionMapper.selectByTaskName(taskName);
    }
}
