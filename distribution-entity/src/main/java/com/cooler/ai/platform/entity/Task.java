package com.cooler.ai.platform.entity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Task {
    private Integer id;

    private String taskName;

    private String domainName;
}