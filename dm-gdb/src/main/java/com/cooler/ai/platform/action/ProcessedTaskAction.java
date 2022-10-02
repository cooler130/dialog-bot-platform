package com.cooler.ai.platform.action;

import java.util.Map;

public interface ProcessedTaskAction<DR, DS, BD> extends TaskAction<DR, DS, BD>{

    Map<String, BD> process();

    String routeNextProcessCode();
}
