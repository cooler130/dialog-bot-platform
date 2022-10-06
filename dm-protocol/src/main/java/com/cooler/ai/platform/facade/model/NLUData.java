package com.cooler.ai.platform.facade.model;

import lombok.Data;
import java.util.List;

@Data
public class NLUData implements java.io.Serializable{
    private List<DomainInfo> result;
}
