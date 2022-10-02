package com.cooler.ai.platform.action;

import com.cooler.ai.platform.entity.Transition;

import java.util.Map;

public interface DataTaskAction<DR, DS, BD> extends TaskAction<DR, DS, BD>{

    Transition getTransition();

    void setTransition(Transition transition);

    Map<String, String> getPreconditionDatasMap();

    void preprocess();

    String getParamValue();

}
