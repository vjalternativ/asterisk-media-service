package com.vizzy.asterisk.astman.bean;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vizzy.asterisk.astman.AsteriskAction;

public class AstWorkflowActionBean {

	private Map<String, AsteriskAction> actionMap = new HashMap<String, AsteriskAction>();
	private List<AsteriskAction> workflow = new LinkedList<AsteriskAction>();

	public Map<String, AsteriskAction> getActionMap() {
		return actionMap;
	}

	public void setActionMap(Map<String, AsteriskAction> actionMap) {
		this.actionMap = actionMap;
	}

	public List<AsteriskAction> getWorkflow() {
		return workflow;
	}

	public void setWorkflow(List<AsteriskAction> workflow) {
		this.workflow = workflow;
	}

}
