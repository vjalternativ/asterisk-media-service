package com.vizzy.asterisk.astman;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.event.DialEndEvent;
import org.asteriskjava.manager.event.DtmfEvent;
import org.asteriskjava.manager.event.HangupEvent;
import org.asteriskjava.manager.event.NewChannelEvent;
import org.asteriskjava.manager.event.NewExtenEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsteriskAction implements IAsteriskEventHandler {

	private String id;
	private String action;
	private Map<String, String> data;
	private AsteriskAction onSuccess;
	private AsteriskAction onFailure;
	private Map<String, String> onEvent;
	private boolean isCompleted;
	private Map<String, String> lastEventDataMap = new HashMap<String, String>();

	private AsteriskAction activeAsteriskAction;
	private boolean isActionExecuted;
	private boolean isActionStarted;
	private boolean isNextActionAvaialble;
	private Map<String, AsteriskAction> actionMap;
	private AsteriskManager manager;
	private String channel;

	private Logger logger = LoggerFactory.getLogger(AsteriskAction.class);

	public AsteriskAction(String id, String action, Map<String, String> data, Map<String, String> onEvent) {
		setId(id);
		setAction(action);
		setData(data);
		setOnEvent(onEvent);
		activeAsteriskAction = this;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public AsteriskAction getOnSuccess() {
		return onSuccess;
	}

	public void setOnSuccess(AsteriskAction onSuccess) {
		this.onSuccess = onSuccess;
	}

	public AsteriskAction getOnFailure() {
		return onFailure;
	}

	public void setOnFailure(AsteriskAction onFailure) {
		this.onFailure = onFailure;
	}

	public Map<String, String> getOnEvent() {
		return onEvent;
	}

	public void setOnEvent(Map<String, String> onEvent) {
		this.onEvent = onEvent;
	}

	public void execute(AsteriskManager manager, String channel)
			throws IllegalArgumentException, IllegalStateException, IOException, TimeoutException {

		logger.info("vjtesting executing action " + action + " for channel" + channel);

		this.manager = manager;
		this.channel = channel;
		lastEventDataMap.remove(action);
		for (Map.Entry<String, String> entry : data.entrySet()) {
			manager.sendSetVarAction(channel, entry.getKey().toUpperCase(), entry.getValue());
		}
		manager.sendRedirectAction(channel, "from-manager-core", getAction(), 1);

	}

	@Override
	public void handleNewExtenEvent(NewExtenEvent event) {

		if (getActiveAsteriskAction().isActionStarted()) {
			if (!event.getExten().equals(getActiveAsteriskAction().getAction())) {
				getActiveAsteriskAction().setActionExecuted();
				processAsteirskActionOnEvent();
			}
		} else {
			if (event.getExten().equals(getActiveAsteriskAction().getAction())) {
				getActiveAsteriskAction().setActionStarted();
			}
		}

		if (getActiveAsteriskAction().isActionExecuted()) {
			getActiveAsteriskAction().setCompleted();
		}

	}

	private void processAsteirskActionOnEvent() {
		// TODO Auto-generated method stub
		if (activeAsteriskAction.getOnEvent() != null) {
			String key = activeAsteriskAction.getLastEventDataMap().get(activeAsteriskAction.getId());
			if (key != null) {
				String nextActionString = activeAsteriskAction.getOnEvent().get(key);
				if (nextActionString != null) {
					AsteriskAction nextAction = activeAsteriskAction.getActionMap().get(nextActionString);
					if (nextAction != null) {
						nextAction.reset();
						nextAction.setActionMap(activeAsteriskAction.getActionMap());
						activeAsteriskAction = nextAction;
						try {
							activeAsteriskAction.execute(manager, channel);
						} catch (IllegalArgumentException | IllegalStateException | IOException | TimeoutException e) {
							// TODO Auto-generated catch block
							activeAsteriskAction.setActionExecuted();
						}
					}
				}

			}
		}
	}

	private void reset() {
		// TODO Auto-generated method stub
		isActionStarted = false;
		isActionExecuted = false;
		isCompleted = false;
	}

	@Override
	public void handleNewChannelEvent(NewChannelEvent event) {

	}

	@Override
	public void handleHangupEvent(HangupEvent event) {

	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted() {
		this.isCompleted = true;
	}

	@Override
	public void handleDtmfEvent(DtmfEvent event) {
		// TODO Auto-generated method stub
		if (event.getDirection().equals("Received")) {

			getActiveAsteriskAction().getLastEventDataMap().put(getActiveAsteriskAction().getId(), event.getDigit());

		}
	}

	public Map<String, String> getLastEventDataMap() {
		return lastEventDataMap;
	}

	public boolean isActionExecuted() {
		return isActionExecuted;
	}

	public void setActionExecuted() {
		this.isActionExecuted = true;
	}

	public boolean isActionStarted() {
		return isActionStarted;
	}

	public void setActionStarted() {
		this.isActionStarted = true;
	}

	public boolean isNextActionAvaialble() {
		return isNextActionAvaialble;
	}

	public void setNextActionAvaialble(boolean isNextActionAvaialble) {
		this.isNextActionAvaialble = isNextActionAvaialble;
	}

	public AsteriskAction getActiveAsteriskAction() {
		return activeAsteriskAction;
	}

	public void setActiveAsteriskAction(AsteriskAction activeAsteriskAction) {
		this.activeAsteriskAction = activeAsteriskAction;
	}

	public Map<String, AsteriskAction> getActionMap() {
		return actionMap;
	}

	public void setActionMap(Map<String, AsteriskAction> actionMap) {
		this.actionMap = actionMap;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void handleDialEndEvent(DialEndEvent event) {
		getActiveAsteriskAction().getLastEventDataMap().put(getActiveAsteriskAction().getId(), event.getDialStatus());
	}

}
