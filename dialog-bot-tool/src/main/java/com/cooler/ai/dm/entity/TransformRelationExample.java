package com.cooler.ai.dm.entity;

import java.util.ArrayList;
import java.util.List;

public class TransformRelationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TransformRelationExample() {
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

        public Criteria andTransformRelationNameIsNull() {
            addCriterion("transform_relation_name is null");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameIsNotNull() {
            addCriterion("transform_relation_name is not null");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameEqualTo(String value) {
            addCriterion("transform_relation_name =", value, "transformRelationName");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameNotEqualTo(String value) {
            addCriterion("transform_relation_name <>", value, "transformRelationName");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameGreaterThan(String value) {
            addCriterion("transform_relation_name >", value, "transformRelationName");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameGreaterThanOrEqualTo(String value) {
            addCriterion("transform_relation_name >=", value, "transformRelationName");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameLessThan(String value) {
            addCriterion("transform_relation_name <", value, "transformRelationName");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameLessThanOrEqualTo(String value) {
            addCriterion("transform_relation_name <=", value, "transformRelationName");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameLike(String value) {
            addCriterion("transform_relation_name like", value, "transformRelationName");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameNotLike(String value) {
            addCriterion("transform_relation_name not like", value, "transformRelationName");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameIn(List<String> values) {
            addCriterion("transform_relation_name in", values, "transformRelationName");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameNotIn(List<String> values) {
            addCriterion("transform_relation_name not in", values, "transformRelationName");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameBetween(String value1, String value2) {
            addCriterion("transform_relation_name between", value1, value2, "transformRelationName");
            return (Criteria) this;
        }

        public Criteria andTransformRelationNameNotBetween(String value1, String value2) {
            addCriterion("transform_relation_name not between", value1, value2, "transformRelationName");
            return (Criteria) this;
        }

        public Criteria andDomainNameIsNull() {
            addCriterion("domain_name is null");
            return (Criteria) this;
        }

        public Criteria andDomainNameIsNotNull() {
            addCriterion("domain_name is not null");
            return (Criteria) this;
        }

        public Criteria andDomainNameEqualTo(String value) {
            addCriterion("domain_name =", value, "domainName");
            return (Criteria) this;
        }

        public Criteria andDomainNameNotEqualTo(String value) {
            addCriterion("domain_name <>", value, "domainName");
            return (Criteria) this;
        }

        public Criteria andDomainNameGreaterThan(String value) {
            addCriterion("domain_name >", value, "domainName");
            return (Criteria) this;
        }

        public Criteria andDomainNameGreaterThanOrEqualTo(String value) {
            addCriterion("domain_name >=", value, "domainName");
            return (Criteria) this;
        }

        public Criteria andDomainNameLessThan(String value) {
            addCriterion("domain_name <", value, "domainName");
            return (Criteria) this;
        }

        public Criteria andDomainNameLessThanOrEqualTo(String value) {
            addCriterion("domain_name <=", value, "domainName");
            return (Criteria) this;
        }

        public Criteria andDomainNameLike(String value) {
            addCriterion("domain_name like", value, "domainName");
            return (Criteria) this;
        }

        public Criteria andDomainNameNotLike(String value) {
            addCriterion("domain_name not like", value, "domainName");
            return (Criteria) this;
        }

        public Criteria andDomainNameIn(List<String> values) {
            addCriterion("domain_name in", values, "domainName");
            return (Criteria) this;
        }

        public Criteria andDomainNameNotIn(List<String> values) {
            addCriterion("domain_name not in", values, "domainName");
            return (Criteria) this;
        }

        public Criteria andDomainNameBetween(String value1, String value2) {
            addCriterion("domain_name between", value1, value2, "domainName");
            return (Criteria) this;
        }

        public Criteria andDomainNameNotBetween(String value1, String value2) {
            addCriterion("domain_name not between", value1, value2, "domainName");
            return (Criteria) this;
        }

        public Criteria andTaskNameIsNull() {
            addCriterion("task_name is null");
            return (Criteria) this;
        }

        public Criteria andTaskNameIsNotNull() {
            addCriterion("task_name is not null");
            return (Criteria) this;
        }

        public Criteria andTaskNameEqualTo(String value) {
            addCriterion("task_name =", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameNotEqualTo(String value) {
            addCriterion("task_name <>", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameGreaterThan(String value) {
            addCriterion("task_name >", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameGreaterThanOrEqualTo(String value) {
            addCriterion("task_name >=", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameLessThan(String value) {
            addCriterion("task_name <", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameLessThanOrEqualTo(String value) {
            addCriterion("task_name <=", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameLike(String value) {
            addCriterion("task_name like", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameNotLike(String value) {
            addCriterion("task_name not like", value, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameIn(List<String> values) {
            addCriterion("task_name in", values, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameNotIn(List<String> values) {
            addCriterion("task_name not in", values, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameBetween(String value1, String value2) {
            addCriterion("task_name between", value1, value2, "taskName");
            return (Criteria) this;
        }

        public Criteria andTaskNameNotBetween(String value1, String value2) {
            addCriterion("task_name not between", value1, value2, "taskName");
            return (Criteria) this;
        }

        public Criteria andContextStateIsNull() {
            addCriterion("context_state is null");
            return (Criteria) this;
        }

        public Criteria andContextStateIsNotNull() {
            addCriterion("context_state is not null");
            return (Criteria) this;
        }

        public Criteria andContextStateEqualTo(String value) {
            addCriterion("context_state =", value, "contextState");
            return (Criteria) this;
        }

        public Criteria andContextStateNotEqualTo(String value) {
            addCriterion("context_state <>", value, "contextState");
            return (Criteria) this;
        }

        public Criteria andContextStateGreaterThan(String value) {
            addCriterion("context_state >", value, "contextState");
            return (Criteria) this;
        }

        public Criteria andContextStateGreaterThanOrEqualTo(String value) {
            addCriterion("context_state >=", value, "contextState");
            return (Criteria) this;
        }

        public Criteria andContextStateLessThan(String value) {
            addCriterion("context_state <", value, "contextState");
            return (Criteria) this;
        }

        public Criteria andContextStateLessThanOrEqualTo(String value) {
            addCriterion("context_state <=", value, "contextState");
            return (Criteria) this;
        }

        public Criteria andContextStateLike(String value) {
            addCriterion("context_state like", value, "contextState");
            return (Criteria) this;
        }

        public Criteria andContextStateNotLike(String value) {
            addCriterion("context_state not like", value, "contextState");
            return (Criteria) this;
        }

        public Criteria andContextStateIn(List<String> values) {
            addCriterion("context_state in", values, "contextState");
            return (Criteria) this;
        }

        public Criteria andContextStateNotIn(List<String> values) {
            addCriterion("context_state not in", values, "contextState");
            return (Criteria) this;
        }

        public Criteria andContextStateBetween(String value1, String value2) {
            addCriterion("context_state between", value1, value2, "contextState");
            return (Criteria) this;
        }

        public Criteria andContextStateNotBetween(String value1, String value2) {
            addCriterion("context_state not between", value1, value2, "contextState");
            return (Criteria) this;
        }

        public Criteria andIntentNamesIsNull() {
            addCriterion("intent_names is null");
            return (Criteria) this;
        }

        public Criteria andIntentNamesIsNotNull() {
            addCriterion("intent_names is not null");
            return (Criteria) this;
        }

        public Criteria andIntentNamesEqualTo(String value) {
            addCriterion("intent_names =", value, "intentNames");
            return (Criteria) this;
        }

        public Criteria andIntentNamesNotEqualTo(String value) {
            addCriterion("intent_names <>", value, "intentNames");
            return (Criteria) this;
        }

        public Criteria andIntentNamesGreaterThan(String value) {
            addCriterion("intent_names >", value, "intentNames");
            return (Criteria) this;
        }

        public Criteria andIntentNamesGreaterThanOrEqualTo(String value) {
            addCriterion("intent_names >=", value, "intentNames");
            return (Criteria) this;
        }

        public Criteria andIntentNamesLessThan(String value) {
            addCriterion("intent_names <", value, "intentNames");
            return (Criteria) this;
        }

        public Criteria andIntentNamesLessThanOrEqualTo(String value) {
            addCriterion("intent_names <=", value, "intentNames");
            return (Criteria) this;
        }

        public Criteria andIntentNamesLike(String value) {
            addCriterion("intent_names like", value, "intentNames");
            return (Criteria) this;
        }

        public Criteria andIntentNamesNotLike(String value) {
            addCriterion("intent_names not like", value, "intentNames");
            return (Criteria) this;
        }

        public Criteria andIntentNamesIn(List<String> values) {
            addCriterion("intent_names in", values, "intentNames");
            return (Criteria) this;
        }

        public Criteria andIntentNamesNotIn(List<String> values) {
            addCriterion("intent_names not in", values, "intentNames");
            return (Criteria) this;
        }

        public Criteria andIntentNamesBetween(String value1, String value2) {
            addCriterion("intent_names between", value1, value2, "intentNames");
            return (Criteria) this;
        }

        public Criteria andIntentNamesNotBetween(String value1, String value2) {
            addCriterion("intent_names not between", value1, value2, "intentNames");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameIsNull() {
            addCriterion("transform_intent_name is null");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameIsNotNull() {
            addCriterion("transform_intent_name is not null");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameEqualTo(String value) {
            addCriterion("transform_intent_name =", value, "transformIntentName");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameNotEqualTo(String value) {
            addCriterion("transform_intent_name <>", value, "transformIntentName");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameGreaterThan(String value) {
            addCriterion("transform_intent_name >", value, "transformIntentName");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameGreaterThanOrEqualTo(String value) {
            addCriterion("transform_intent_name >=", value, "transformIntentName");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameLessThan(String value) {
            addCriterion("transform_intent_name <", value, "transformIntentName");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameLessThanOrEqualTo(String value) {
            addCriterion("transform_intent_name <=", value, "transformIntentName");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameLike(String value) {
            addCriterion("transform_intent_name like", value, "transformIntentName");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameNotLike(String value) {
            addCriterion("transform_intent_name not like", value, "transformIntentName");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameIn(List<String> values) {
            addCriterion("transform_intent_name in", values, "transformIntentName");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameNotIn(List<String> values) {
            addCriterion("transform_intent_name not in", values, "transformIntentName");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameBetween(String value1, String value2) {
            addCriterion("transform_intent_name between", value1, value2, "transformIntentName");
            return (Criteria) this;
        }

        public Criteria andTransformIntentNameNotBetween(String value1, String value2) {
            addCriterion("transform_intent_name not between", value1, value2, "transformIntentName");
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