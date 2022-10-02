package com.cooler.ai.platform.action;

import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DialogState;

import java.util.Map;

public class BaseProcessedTaskAction extends BaseTaskAction implements ProcessedTaskAction<DMRequest, DialogState, BizDataModelState<String>> {

    @Override
    public Map<String, BizDataModelState<String>> process() {
        return null;
    }

    @Override
    public String routeNextProcessCode() {
        return null;
    }
}
