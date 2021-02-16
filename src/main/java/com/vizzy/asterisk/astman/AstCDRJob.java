package com.vizzy.asterisk.astman;

import java.io.IOException;
import java.util.Map;

import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.event.DialEndEvent;
import org.asteriskjava.manager.event.DtmfEvent;
import org.asteriskjava.manager.event.HangupEvent;
import org.asteriskjava.manager.event.NewChannelEvent;
import org.asteriskjava.manager.event.NewExtenEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstCDRJob implements IAsteriskEventHandler {

	private AsteriskManager manager;
	private AsteriskAction asteriskAction;
	private boolean jobInProgress = false;
	private boolean isJobCompleted = false;
	private boolean jobStarted = false;
	private String lastExten;
	private String channel;
	private static Logger logger = LoggerFactory.getLogger(AstCDRJob.class);
	private Map<String, AsteriskAction> actionMap;

	protected AstCDRJob(AsteriskManager manager, AsteriskAction astAction, Map<String, AsteriskAction> map) {
		setAsteriskAction(astAction);
		setActionMap(map);
		getAsteriskAction().setActionMap(map);
		setManager(manager);
	}

	boolean isJobInProgress() {
		return jobInProgress;
	}

	void setJobInProgress(boolean jobInProgress) {
		this.jobInProgress = jobInProgress;
	}

	boolean execute() {
		markJobInProgress();

		try {

			getAsteriskAction().execute(manager, channel);

		} catch (IllegalArgumentException | IllegalStateException | IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	protected void markJobCompleted() {

		if (!isJobCompleted()) {
			setJobCompleted(true);
			setJobInProgress(false);
			setJobStarted(false);
			logger.info(getAsteriskAction().getAction() + " job completed for channel " + channel);
		}
	}

	protected void markJobStarted() {
		if (!isJobStarted()) {
			setJobStarted(true);
			logger.info(getAsteriskAction().getAction() + " job started for channel " + channel);
		}
	}

	protected void markJobInProgress() {
		setJobInProgress(true);
		logger.info(getAsteriskAction().getAction() + " job inprogress for channel " + channel);
	}

	public AsteriskManager getManager() {
		return manager;
	}

	public void setManager(AsteriskManager manager) {
		this.manager = manager;
	}

	public boolean isJobCompleted() {
		return isJobCompleted;
	}

	public void setJobCompleted(boolean isJobCompleted) {
		this.isJobCompleted = isJobCompleted;
	}

	@Override
	public void handleNewExtenEvent(NewExtenEvent event) {
		// TODO Auto-generated method stub

		if (!isJobStarted() && event.getExtension().equals(getAsteriskAction().getAction())) {
			markJobStarted();
		} else {

			if (isJobStarted()) {
				getAsteriskAction().handleNewExtenEvent(event);
			}
		}

		if (getAsteriskAction().getActiveAsteriskAction().isCompleted()) {
			markJobCompleted();
		}
		setLastExten(event.getExtension());

	}

	@Override
	public void handleNewChannelEvent(NewChannelEvent event) {

	}

	@Override
	public void handleHangupEvent(HangupEvent event) {
		markJobCompleted();
	}

	public String getLastExten() {
		return lastExten;
	}

	public void setLastExten(String lastExten) {
		this.lastExten = lastExten;
	}

	public boolean isJobStarted() {
		return jobStarted;

	}

	public void setJobStarted(boolean jobStarted) {
		this.jobStarted = jobStarted;

	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	@Override
	public void handleDtmfEvent(DtmfEvent event) {
		getAsteriskAction().handleDtmfEvent(event);
	}

	public AsteriskAction getAsteriskAction() {
		return asteriskAction;
	}

	public void setAsteriskAction(AsteriskAction activeAsteriskAction) {
		this.asteriskAction = activeAsteriskAction;
	}

	public Map<String, AsteriskAction> getActionMap() {
		return actionMap;
	}

	public void setActionMap(Map<String, AsteriskAction> actionMap) {
		this.actionMap = actionMap;
	}

	@Override
	public void handleDialEndEvent(DialEndEvent event) {
		// TODO Auto-generated method stub
		getAsteriskAction().handleDialEndEvent(event);
	}

}
