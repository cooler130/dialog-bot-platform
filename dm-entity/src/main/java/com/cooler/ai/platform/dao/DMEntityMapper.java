package com.cooler.ai.platform.dao;

import com.cooler.ai.platform.entity.*;

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