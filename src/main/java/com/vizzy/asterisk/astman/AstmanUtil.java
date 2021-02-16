package com.vizzy.asterisk.astman;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vizzy.asterisk.astman.bean.AstWorkflowActionBean;
import com.vizzy.media.util.JSONUtil;

public class AstmanUtil {

	private static Map<String, String> getOnEventAsteriskActionMap(JSONObject ob) {
		Map<String, String> onEventMap = new HashMap<String, String>();
		try {

			Map<String, Object> data = JSONUtil.toMap(ob.getJSONObject("onEvent"));
			for (Map.Entry<String, Object> entry : data.entrySet()) {

				if (entry.getValue() instanceof String) {
					onEventMap.put(entry.getKey(), (String) entry.getValue());
				}

			}
		} catch (JSONException e) {
			return onEventMap;
		}
		return onEventMap;
	}

	public static AsteriskAction getAsteriskActionFromJsonObject(String id, JSONObject ob) {
		String action = ob.getString("action");

		Map<String, String> hashMapData = new HashMap<String, String>();
		Map<String, Object> data = JSONUtil.toMap(ob.getJSONObject("data"));
		Map<String, String> onEventAsteriskActionMap = getOnEventAsteriskActionMap(ob);

		for (Map.Entry<String, Object> entry : data.entrySet()) {
			if (entry.getValue() instanceof String) {
				hashMapData.put(entry.getKey(), (String) entry.getValue());
			}
		}
		return new AsteriskAction(id, action, hashMapData, onEventAsteriskActionMap);
	}

	public static AstWorkflowActionBean getAsteriskWorkflowActionBeanFromJsonString(String json) throws JSONException {
		AstWorkflowActionBean workflowBean = new AstWorkflowActionBean();

		JSONObject jsonob = new JSONObject(json);

		JSONObject actionMapJson = jsonob.getJSONObject("actionMap");
		if (actionMapJson == null) {
			throw new JSONException("action map not defined");
		}
		JSONArray workflow = jsonob.getJSONArray("workflow");
		if (workflow == null) {
			throw new JSONException("workflow not defined");
		}

		JSONArray workflowArray = (JSONArray) workflow;
		Map<String, JSONObject> actionMap = JSONUtil.toJSONObjectMap(actionMapJson);

		Map<String, AsteriskAction> asteriskActionMap = new HashMap<String, AsteriskAction>();
		for (Map.Entry<String, JSONObject> entry : actionMap.entrySet()) {
			asteriskActionMap.put(entry.getKey(), getAsteriskActionFromJsonObject(entry.getKey(), entry.getValue()));
		}
		workflowBean.setActionMap(asteriskActionMap);

		int i;
		for (i = 0; i < workflowArray.length(); i++) {

			Object action = workflowArray.get(i);
			if (action instanceof String) {

				AsteriskAction astAction = asteriskActionMap.get(action);

				if (astAction != null) {
					workflowBean.getWorkflow().add(astAction);
				}
			}
		}

		if (workflowBean.getWorkflow().size() == 0) {
			throw new JSONException("invalid workflow");

		}

		return workflowBean;
	}
}
