package com.cooler.ai.dm.model;

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
        boolean bothY1 = conditionWhether.equals("") && that.conditionWhether.equals("Y");
        boolean bothY2 = conditionWhether.equals("Y") && that.conditionWhether.equals("");
        return Objects.equals(conditionName, that.conditionName) &&
                (Objects.equals(conditionWhether, that.conditionWhether) || bothY1 || bothY2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditionName, conditionWhether);
    }
}
