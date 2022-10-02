package com.cooler.ai.platform.model;

import com.alibaba.fastjson.JSON;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.impl.AbstractUntypedStateMachine;

@StateMachineParameters(stateType = Integer.class, eventType = String.class, contextType = ConditionContext.class)
public class StateMachineSample extends AbstractUntypedStateMachine {

    protected void exitOne(Integer from, Integer to, String event, ConditionContext context) {
        System.out.println("离开 \'" + from + "\' 状态.");
    }
    protected void fromOneToOne(Integer from, Integer to, String event, ConditionContext context) {
        System.out.println("迁移 从 '" + from + "'状态到 '" + to + "'状态，发生事件： '" + event + "' 上下文： '" + JSON.toJSONString(context) + "'.");
    }
    protected void ontoOne(Integer from, Integer to, String event, ConditionContext context) {
        System.out.println("进入 状态 \'" + to + "\'.");
    }

}