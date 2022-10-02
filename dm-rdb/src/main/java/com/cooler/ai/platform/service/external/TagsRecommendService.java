package com.cooler.ai.platform.service.external;

import java.util.Map;
import java.util.List;

public interface TagsRecommendService<T> {

    String getLabels(List<T> resultlist, Map<String, List<String>> slotvalus);
}
