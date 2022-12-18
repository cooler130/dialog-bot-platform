package com.cooler.ai.dm.entity;

import java.util.ArrayList;
import java.util.List;

public class PolicyConditionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PolicyConditionExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andPolicyIdIsNull() {
            addCriterion("policy_id is null");
            return (Criteria) this;
        }

        public Criteria andPolicyIdIsNotNull() {
            addCriterion("policy_id is not null");
            return (Criteria) this;
        }

        public Criteria andPolicyIdEqualTo(Integer value) {
            addCriterion("policy_id =", value, "policyId");
            return (Criteria) this;
        }

        public Criteria andPolicyIdNotEqualTo(Integer value) {
            addCriterion("policy_id <>", value, "policyId");
            return (Criteria) this;
        }

        public Criteria andPolicyIdGreaterThan(Integer value) {
            addCriterion("policy_id >", value, "policyId");
            return (Criteria) this;
        }

        public Criteria andPolicyIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("policy_id >=", value, "policyId");
            return (Criteria) this;
        }

        public Criteria andPolicyIdLessThan(Integer value) {
            addCriterion("policy_id <", value, "policyId");
            return (Criteria) this;
        }

        public Criteria andPolicyIdLessThanOrEqualTo(Integer value) {
            addCriterion("policy_id <=", value, "policyId");
            return (Criteria) this;
        }

        public Criteria andPolicyIdIn(List<Integer> values) {
            addCriterion("policy_id in", values, "policyId");
            return (Criteria) this;
        }

        public Criteria andPolicyIdNotIn(List<Integer> values) {
            addCriterion("policy_id not in", values, "policyId");
            return (Criteria) this;
        }

        public Criteria andPolicyIdBetween(Integer value1, Integer value2) {
            addCriterion("policy_id between", value1, value2, "policyId");
            return (Criteria) this;
        }

        public Criteria andPolicyIdNotBetween(Integer value1, Integer value2) {
            addCriterion("policy_id not between", value1, value2, "policyId");
            return (Criteria) this;
        }

        public Criteria andConditionNameIsNull() {
            addCriterion("condition_name is null");
            return (Criteria) this;
        }

        public Criteria andConditionNameIsNotNull() {
            addCriterion("condition_name is not null");
            return (Criteria) this;
        }

        public Criteria andConditionNameEqualTo(String value) {
            addCriterion("condition_name =", value, "conditionName");
            return (Criteria) this;
        }

        public Criteria andConditionNameNotEqualTo(String value) {
            addCriterion("condition_name <>", value, "conditionName");
            return (Criteria) this;
        }

        public Criteria andConditionNameGreaterThan(String value) {
            addCriterion("condition_name >", value, "conditionName");
            return (Criteria) this;
        }

        public Criteria andConditionNameGreaterThanOrEqualTo(String value) {
            addCriterion("condition_name >=", value, "conditionName");
            return (Criteria) this;
        }

        public Criteria andConditionNameLessThan(String value) {
            addCriterion("condition_name <", value, "conditionName");
            return (Criteria) this;
        }

        public Criteria andConditionNameLessThanOrEqualTo(String value) {
            addCriterion("condition_name <=", value, "conditionName");
            return (Criteria) this;
        }

        public Criteria andConditionNameLike(String value) {
            addCriterion("condition_name like", value, "conditionName");
            return (Criteria) this;
        }

        public Criteria andConditionNameNotLike(String value) {
            addCriterion("condition_name not like", value, "conditionName");
            return (Criteria) this;
        }

        public Criteria andConditionNameIn(List<String> values) {
            addCriterion("condition_name in", values, "conditionName");
            return (Criteria) this;
        }

        public Criteria andConditionNameNotIn(List<String> values) {
            addCriterion("condition_name not in", values, "conditionName");
            return (Criteria) this;
        }

        public Criteria andConditionNameBetween(String value1, String value2) {
            addCriterion("condition_name between", value1, value2, "conditionName");
            return (Criteria) this;
        }

        public Criteria andConditionNameNotBetween(String value1, String value2) {
            addCriterion("condition_name not between", value1, value2, "conditionName");
            return (Criteria) this;
        }

        public Criteria andConditionWhetherIsNull() {
            addCriterion("condition_whether is null");
            return (Criteria) this;
        }

        public Criteria andConditionWhetherIsNotNull() {
            addCriterion("condition_whether is not null");
            return (Criteria) this;
        }

        public Criteria andConditionWhetherEqualTo(Byte value) {
            addCriterion("condition_whether =", value, "conditionWhether");
            return (Criteria) this;
        }

        public Criteria andConditionWhetherNotEqualTo(Byte value) {
            addCriterion("condition_whether <>", value, "conditionWhether");
            return (Criteria) this;
        }

        public Criteria andConditionWhetherGreaterThan(Byte value) {
            addCriterion("condition_whether >", value, "conditionWhether");
            return (Criteria) this;
        }

        public Criteria andConditionWhetherGreaterThanOrEqualTo(Byte value) {
            addCriterion("condition_whether >=", value, "conditionWhether");
            return (Criteria) this;
        }

        public Criteria andConditionWhetherLessThan(Byte value) {
            addCriterion("condition_whether <", value, "conditionWhether");
            return (Criteria) this;
        }

        public Criteria andConditionWhetherLessThanOrEqualTo(Byte value) {
            addCriterion("condition_whether <=", value, "conditionWhether");
            return (Criteria) this;
        }

        public Criteria andConditionWhetherIn(List<Byte> values) {
            addCriterion("condition_whether in", values, "conditionWhether");
            return (Criteria) this;
        }

        public Criteria andConditionWhetherNotIn(List<Byte> values) {
            addCriterion("condition_whether not in", values, "conditionWhether");
            return (Criteria) this;
        }

        public Criteria andConditionWhetherBetween(Byte value1, Byte value2) {
            addCriterion("condition_whether between", value1, value2, "conditionWhether");
            return (Criteria) this;
        }

        public Criteria andConditionWhetherNotBetween(Byte value1, Byte value2) {
            addCriterion("condition_whether not between", value1, value2, "conditionWhether");
            return (Criteria) this;
        }

        public Criteria andConditionTextIsNull() {
            addCriterion("condition_text is null");
            return (Criteria) this;
        }

        public Criteria andConditionTextIsNotNull() {
            addCriterion("condition_text is not null");
            return (Criteria) this;
        }

        public Criteria andConditionTextEqualTo(String value) {
            addCriterion("condition_text =", value, "conditionText");
            return (Criteria) this;
        }

        public Criteria andConditionTextNotEqualTo(String value) {
            addCriterion("condition_text <>", value, "conditionText");
            return (Criteria) this;
        }

        public Criteria andConditionTextGreaterThan(String value) {
            addCriterion("condition_text >", value, "conditionText");
            return (Criteria) this;
        }

        public Criteria andConditionTextGreaterThanOrEqualTo(String value) {
            addCriterion("condition_text >=", value, "conditionText");
            return (Criteria) this;
        }

        public Criteria andConditionTextLessThan(String value) {
            addCriterion("condition_text <", value, "conditionText");
            return (Criteria) this;
        }

        public Criteria andConditionTextLessThanOrEqualTo(String value) {
            addCriterion("condition_text <=", value, "conditionText");
            return (Criteria) this;
        }

        public Criteria andConditionTextLike(String value) {
            addCriterion("condition_text like", value, "conditionText");
            return (Criteria) this;
        }

        public Criteria andConditionTextNotLike(String value) {
            addCriterion("condition_text not like", value, "conditionText");
            return (Criteria) this;
        }

        public Criteria andConditionTextIn(List<String> values) {
            addCriterion("condition_text in", values, "conditionText");
            return (Criteria) this;
        }

        public Criteria andConditionTextNotIn(List<String> values) {
            addCriterion("condition_text not in", values, "conditionText");
            return (Criteria) this;
        }

        public Criteria andConditionTextBetween(String value1, String value2) {
            addCriterion("condition_text between", value1, value2, "conditionText");
            return (Criteria) this;
        }

        public Criteria andConditionTextNotBetween(String value1, String value2) {
            addCriterion("condition_text not between", value1, value2, "conditionText");
            return (Criteria) this;
        }

        public Criteria andEnableIsNull() {
            addCriterion("enable is null");
            return (Criteria) this;
        }

        public Criteria andEnableIsNotNull() {
            addCriterion("enable is not null");
            return (Criteria) this;
        }

        public Criteria andEnableEqualTo(Byte value) {
            addCriterion("enable =", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotEqualTo(Byte value) {
            addCriterion("enable <>", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableGreaterThan(Byte value) {
            addCriterion("enable >", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableGreaterThanOrEqualTo(Byte value) {
            addCriterion("enable >=", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableLessThan(Byte value) {
            addCriterion("enable <", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableLessThanOrEqualTo(Byte value) {
            addCriterion("enable <=", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableIn(List<Byte> values) {
            addCriterion("enable in", values, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotIn(List<Byte> values) {
            addCriterion("enable not in", values, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableBetween(Byte value1, Byte value2) {
            addCriterion("enable between", value1, value2, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotBetween(Byte value1, Byte value2) {
            addCriterion("enable not between", value1, value2, "enable");
            return (Criteria) this;
        }

        public Criteria andMsgIsNull() {
            addCriterion("msg is null");
            return (Criteria) this;
        }

        public Criteria andMsgIsNotNull() {
            addCriterion("msg is not null");
            return (Criteria) this;
        }

        public Criteria andMsgEqualTo(String value) {
            addCriterion("msg =", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgNotEqualTo(String value) {
            addCriterion("msg <>", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgGreaterThan(String value) {
            addCriterion("msg >", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgGreaterThanOrEqualTo(String value) {
            addCriterion("msg >=", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgLessThan(String value) {
            addCriterion("msg <", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgLessThanOrEqualTo(String value) {
            addCriterion("msg <=", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgLike(String value) {
            addCriterion("msg like", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgNotLike(String value) {
            addCriterion("msg not like", value, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgIn(List<String> values) {
            addCriterion("msg in", values, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgNotIn(List<String> values) {
            addCriterion("msg not in", values, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgBetween(String value1, String value2) {
            addCriterion("msg between", value1, value2, "msg");
            return (Criteria) this;
        }

        public Criteria andMsgNotBetween(String value1, String value2) {
            addCriterion("msg not between", value1, value2, "msg");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}