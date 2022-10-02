package com.cooler.ai.platform.action;

import com.cooler.ai.platform.facade.model.DMResponse;

public interface InteractiveTaskAction<DR, DS, BD> extends TaskAction<DR, DS, BD>{

    void setProcessActionName(String processActionName);

    DMResponse interActive();

}
