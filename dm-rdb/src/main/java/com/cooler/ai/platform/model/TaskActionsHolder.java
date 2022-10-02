package com.cooler.ai.platform.model;

import com.cooler.ai.platform.action.TaskAction;
import lombok.Data;

import java.util.Map;

@Data
public class TaskActionsHolder {

    private Map<String, TaskAction> taskActionMap;

}
