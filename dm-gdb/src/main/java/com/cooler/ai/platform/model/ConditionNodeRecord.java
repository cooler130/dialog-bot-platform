package com.cooler.ai.platform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionNodeRecord {
    private String SUID;

    private String conditionName;
    private String conditionWhether;

    public ConditionNodeRecord(String conditionName, String conditionWhether) {
        this.conditionName = conditionName;
        this.conditionWhether = conditionWhether;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConditionNodeRecord that = (ConditionNodeRecord) o;
        return Objects.equals(conditionName, that.conditionName) &&
                Objects.equals(conditionWhether, that.conditionWhether);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditionName, conditionWhether);
    }
}
