package com.cooler.ai.platform.service2;

import com.cooler.ai.platform.entity2.Transition;
import java.util.List;

public interface TransitionService {

    List<Transition> selectByTaskStartStateId(String taskName, Integer currentStateId, String intentName);

    List<Transition> selectByTaskName(String taskName);
}
