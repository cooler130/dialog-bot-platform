package com.cooler.ai.dm;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.platform.entity.*;
import com.cooler.ai.platform.service.framework.DMEntityService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JSONMapMaker {

	public static final String JSON_FILE_PATH = "DMData.json";													//这个文件生成后，手动放到resources/data下


	public static void main(String[] args) {
		BeanFactory beanFactory = new ClassPathXmlApplicationContext("applicationContext-json-map.xml");
		DMEntityService dmEntityService = (DMEntityService) beanFactory.getBean("dmEntityService");

//		List<Intent> intents = dmEntityService.selectAllIntent();
//		List<NLUIntent> nluIntents = dmEntityService.selectAllNLUIntent();
//		List<NLUSlot> nluSlots = dmEntityService.selectAllNLUSlot();
//		List<Slot> slots = dmEntityService.selectAllSlot();
//		List<SlotRelation> slotRelations = dmEntityService.selectAllSlotRelation();


		List<Action> actions = dmEntityService.selectAllAction();
		List<ConditionLogic> conditionLogics = dmEntityService.selectAllConditionLogic();
		List<ConditionRule> conditionRules = dmEntityService.selectAllConditionRule();
		List<Policy> policies = dmEntityService.selectAllPolicy();
		List<State> states = dmEntityService.selectAllState();
		List<Transition> transitions = dmEntityService.selectAllTransition();
		List<ConditionKV> conditionKVS = dmEntityService.selectAllConditionKV();

		Map<String, String> totalMap = new TreeMap<>();


//		//Intent:	按照Id查询单个；	按照domainName和intentName查询单个
//		for (Intent intent : intents) {
//			totalMap.put("Intent_" + intent.getId(), JSON.toJSONString(intent));
//			totalMap.put("Intent_" + intent.getDomainName() + "_" + intent.getIntentName(), JSON.toJSONString(intent));
//		}
//
//		//NLUIntent:	按照nluDomainName和nluIntentName查询
//		for (NLUIntent nluIntent : nluIntents) {
//			totalMap.put("NLUIntent_" + nluIntent.getNluDomainName() + "_" + nluIntent.getNluIntentName(), JSON.toJSONString(nluIntent));
//		}
//
//		//NLUSlot:	按照id查询单个
//		for (NLUSlot nluSlot : nluSlots) {
//			Integer id = nluSlot.getId();
//			totalMap.put("NLUSlot_" + id, JSON.toJSONString(nluSlot));
//		}
//
//		//Slot：		按intentId查询集合
//		Map<Integer, List<Slot>> slotsMap = new HashMap<>();
//		for (Slot slot : slots) {
//			Integer intentId = slot.getIntentId();
//			List<Slot> slotsGroup = slotsMap.get(intentId);
//			if(slotsGroup == null){
//				slotsGroup = new ArrayList<>();
//			}
//			slotsGroup.add(slot);
//			slotsMap.put(intentId, slotsGroup);
//		}
//		for (Integer intentId : slotsMap.keySet()) {
//			List<Slot> slotsGroup = slotsMap.get(intentId);
//			totalMap.put("Slot_" + intentId, JSON.toJSONString(slotsGroup));
//		}
//
//		//SlotRelation:		按slotIds查询nluSlotIds集合
//		Map<Integer, List<SlotRelation>> slotRelationMap = new HashMap<>();
//		for (SlotRelation slotRelation : slotRelations) {
//			Integer slotId = slotRelation.getSlotId();
//			List<SlotRelation> slotRelationsGroup = slotRelationMap.get(slotId);
//			if(slotRelationsGroup == null){
//				slotRelationsGroup = new ArrayList<>();
//			}
//			slotRelationsGroup.add(slotRelation);
//			slotRelationMap.put(slotId, slotRelationsGroup);
//		}
//		for (Integer slotId : slotRelationMap.keySet()) {
//			List<SlotRelation> slotRelationsGroup = slotRelationMap.get(slotId);
//			totalMap.put("SlotRelation_" + slotId, JSON.toJSONString(slotRelationsGroup));
//		}

		//Action:	按照Id、processCode，查询单个动作
		for (Action action : actions) {
			String actionJS = JSON.toJSONString(action);
			totalMap.put("Action_" + action.getId(), actionJS);
			totalMap.put("Action_" + action.getProcessCode(), actionJS);
		}

		//ConditionLogic:	按照transitionId查询集合
		Map<Integer, List<ConditionLogic>> conditionLogicsMap = new HashMap<>();
		for (ConditionLogic conditionLogic : conditionLogics) {
			Integer transitionId = conditionLogic.getTransitionId();
			List<ConditionLogic> conditionLogicsGroup = conditionLogicsMap.get(transitionId);
			if(conditionLogicsGroup == null){
				conditionLogicsGroup = new ArrayList<>();
			}
			conditionLogicsGroup.add(conditionLogic);
			conditionLogicsMap.put(transitionId, conditionLogicsGroup);
		}
		for (Integer transitionId : conditionLogicsMap.keySet()) {
			List<ConditionLogic> conditionLogicsGroup = conditionLogicsMap.get(transitionId);
			totalMap.put("ConditionLogic_" + transitionId, JSON.toJSONString(conditionLogicsGroup));
		}

		//ConditionRule:	按照Id查询单个；	按照taskId查询集合
		Map<String, List<ConditionRule>> conditionRulesMap = new HashMap<>();
		for (ConditionRule conditionRule : conditionRules) {
			totalMap.put("ConditionRule_" + conditionRule.getId(), JSON.toJSONString(conditionRule));

			Integer taskId = conditionRule.getTaskId();
			List<ConditionRule> conditionRulesGroup = conditionRulesMap.get(taskId + "");
			if(conditionRulesGroup == null){
				conditionRulesGroup = new ArrayList<>();
			}
			conditionRulesGroup.add(conditionRule);
			conditionRulesMap.put(taskId + "", conditionRulesGroup);
		}
		for (String key : conditionRulesMap.keySet()) {
			List<ConditionRule> conditionRulesGroup = conditionRulesMap.get(key);
			totalMap.put("ConditionRule_" + key, JSON.toJSONString(conditionRulesGroup));
		}

		//Policy:	按照stateId查询集合
		Map<Integer, List<Policy>> policiesMap = new HashMap<>();
		for (Policy policy : policies) {
			Integer stateId = policy.getStateId();
			List<Policy> policiesGroup = policiesMap.get(stateId);
			if(policiesGroup == null){
				policiesGroup = new ArrayList<>();
			}
			policiesGroup.add(policy);
			policiesMap.put(stateId, policiesGroup);
		}
		for (Integer stateId : policiesMap.keySet()) {
			List<Policy> policiesGroup = policiesMap.get(stateId);
			totalMap.put("Policy_" + stateId, JSON.toJSONString(policiesGroup));
		}



		//State:	按domainId和intentId查询集合；按Id查询单个
		for (State state : states) {
			totalMap.put("State_" + state.getId(), JSON.toJSONString(state));
		}

		//Transition:	按domainId、intentId和startStateId查询集合；按domainId、intentId查询集合
		Map<String, List<Transition>> transitionsMap1 = new HashMap<>();
		Map<String, List<Transition>> transitionsMap2 = new HashMap<>();
		for (Transition transition : transitions) {
			String taskName = transition.getTaskName();
			Integer startStateId = transition.getStartStateId();

			String key1 = taskName + "_" + startStateId;
			List<Transition> transitionsGroup1 = transitionsMap1.get(key1);
			if(transitionsGroup1 == null){
				transitionsGroup1 = new ArrayList<>();
			}
			transitionsGroup1.add(transition);
			transitionsMap1.put(key1, transitionsGroup1);

			String key2 = taskName + "";
			List<Transition> transitionsGroup2 = transitionsMap2.get(key2);
			if(transitionsGroup2 == null){
				transitionsGroup2 = new ArrayList<>();
			}
			transitionsGroup2.add(transition);
			transitionsMap2.put(key2, transitionsGroup2);
		}
		for (String key1 : transitionsMap1.keySet()) {
			List<Transition> transitionsGroup1 = transitionsMap1.get(key1);
			totalMap.put("Transition_" + key1, JSON.toJSONString(transitionsGroup1));
		}
		for (String key2 : transitionsMap2.keySet()) {
			List<Transition> transitionsGroup2 = transitionsMap2.get(key2);
			totalMap.put("Transition_" + key2, JSON.toJSONString(transitionsGroup2));
		}

		//ConditionKV: 按policyId查询ConditionKV集合
		Map<Integer, List<ConditionKV>> conditionKVsMap = new HashMap<>();
		for (ConditionKV conditionKV : conditionKVS) {
			Integer policyId = conditionKV.getPolicyId();
			List<ConditionKV> conditionKVSTmp = conditionKVsMap.get(policyId);
			if(conditionKVSTmp == null){
				conditionKVSTmp = new ArrayList<>();
			}
			conditionKVSTmp.add(conditionKV);
			conditionKVsMap.put(policyId, conditionKVSTmp);
		}
		for (Integer key : conditionKVsMap.keySet()) {
			List<ConditionKV> conditionKVSTmp = conditionKVsMap.get(key);
			totalMap.put("ConditionKV_" + key, JSON.toJSONString(conditionKVSTmp));
		}

		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;
		try {
			File jsonFile = new File(JSON_FILE_PATH);
			fileWriter = new FileWriter(jsonFile);
			bufferWriter = new BufferedWriter(fileWriter);
			bufferWriter.write(JSON.toJSONString(totalMap));

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			try {
				bufferWriter.close();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
	}
}