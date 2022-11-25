package com.cooler.ai.platform.dao2;

import com.cooler.ai.platform.entity2.*;

import java.util.List;

public interface DMEntityMapper {

    List<Action> selectAllAction();

    List<ConditionKV> selectAllConditionKV();

    List<ConditionLogic> selectAllConditionLogic();

    List<ConditionRule> selectAllConditionRule();

    List<Policy> selectAllPolicy();

    List<State> selectAllState();

    List<Transition> selectAllTransition();

}