package com.vizzy.asterisk.astman;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.asteriskjava.manager.event.DialEndEvent;
import org.asteriskjava.manager.event.DtmfEvent;
import org.asteriskjava.manager.event.HangupEvent;
import org.asteriskjava.manager.event.ManagerEvent;
import org.asteriskjava.manager.event.NewChannelEvent;
import org.asteriskjava.manager.event.NewExtenEvent;
import org.asteriskjava.manager.event.RtcpSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vizzy.ast.media.service.ICallHandler;
import com.vizzy.asterisk.astman.bean.AstCDRHistory;
import com.vizzy.asterisk.astman.bean.AstWorkflowActionBean;

public class AsteriskEventManager extends Thread {

	private AsteriskManager manager;
	private static Logger logger = LoggerFactory.getLogger(AsteriskEventManager.class);
	private AsteriskEventHandler eventHandler;
	private Map<String, Queue<AstCDRJob>> channelVsAstCDRJobQueue = new HashMap<String, Queue<AstCDRJob>>();
	private List<String> allowedWorkflowActions = new LinkedList<String>();

	public AsteriskEventManager(AsteriskManager asteriskManager, List<String> allowedWorkflowActionsList,
			ICallHandler callHandler) {

		setManager(asteriskManager);

		setAllowedWorkflowActions(allowedWorkflowActionsList);

		setEventHandler(new AsteriskEventHandler(asteriskManager, this, callHandler));
		eventHandler.start();
	}

	public AsteriskManager getManager() {
		return manager;
	}

	public void setManager(AsteriskManager manager) {
		this.manager = manager;
	}

	public void run() {
		while (true) {
			ManagerEvent event = manager.getEventQueue().poll();
			if (event == null) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				handleManagerEvent(event);
			}
		}
	}

	private AstCDRJob getChannelInProgressJob(String channel) {
		Queue<AstCDRJob> cdrJobQueue = getChannelVsAstCDRJobQueue().get(channel);

		if (cdrJobQueue != null) {
			AstCDRJob job = cdrJobQueue.peek();
			if (job != null) {
				if (job.isJobInProgress()) {
					return job;
				}

			}
		}
		return null;
	}

	private void executeNexJob(String channel) {
		Queue<AstCDRJob> cdrJobQueue = getChannelVsAstCDRJobQueue().get(channel);

		cdrJobQueue.poll();
		if (cdrJobQueue != null) {
			AstCDRJob newJob = cdrJobQueue.peek();
			if (newJob != null && newJob.isJobInProgress() == false) {
				newJob.execute();
			}
		}
	}

	private void handleManagerEventForCDRJobQueue(ManagerEvent event) {

		String channel = null;
		AstCDRJob job;
		if (event instanceof HangupEvent || event instanceof NewExtenEvent) {

			if (event instanceof HangupEvent) {
				channel = ((HangupEvent) event).getChannel();
			} else if (event instanceof NewExtenEvent) {
				channel = ((NewExtenEvent) event).getChannel();
			}

			job = getChannelInProgressJob(channel);
			if (job != null) {
				if (event instanceof HangupEvent) {
					job.handleHangupEvent((HangupEvent) event);
				} else if (event instanceof NewExtenEvent) {
					job.handleNewExtenEvent((NewExtenEvent) event);
				}

				if (job.isJobCompleted()) {
					executeNexJob(channel);
				}
			}

		} else if (event instanceof DtmfEvent) {

			channel = ((DtmfEvent) event).getChannel();
			job = getChannelInProgressJob(channel);
			if (job != null) {
				job.handleDtmfEvent((DtmfEvent) event);
			}

		} else if (event instanceof DialEndEvent) {
			channel = ((DialEndEvent) event).getChannel();
			job = getChannelInProgressJob(channel);
			if (job != null) {
				job.handleDialEndEvent((DialEndEvent) event);
			}
		}

	}

	void handleManagerEvent(ManagerEvent event) {

		if (event instanceof NewChannelEvent) {
			eventHandler.handleNewChannelEvent((NewChannelEvent) event);
		} else if (event instanceof HangupEvent) {
			eventHandler.handleHangupEvent((HangupEvent) event);

		} else if (event instanceof NewExtenEvent) {
			eventHandler.handleNewExtenEvent((NewExtenEvent) event);
		} else if (event instanceof RtcpSentEvent) {
		} else if (event instanceof DialEndEvent) {

			eventHandler.handleDialEndEvent((DialEndEvent) event);
		}
		handleManagerEventForCDRJobQueue(event);
	}

	public AsteriskEventHandler getEventHandler() {
		return eventHandler;
	}

	public void setEventHandler(AsteriskEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	private void addJobToCDRJobQueue(Queue<AstCDRJob> astCDRJobQueue, AstCDRHistory cdr,
			AstWorkflowActionBean workflowBean) {
		for (int i = 0; i < workflowBean.getWorkflow().size(); i++) {
			AstCDRJob job = new AstCDRJob(manager, workflowBean.getWorkflow().get(i), workflowBean.getActionMap());
			job.setChannel(cdr.getChannel());
			astCDRJobQueue.add(job);

		}

	}

	public void addNewActionToChannelVsAstCDRJobQueue(AstCDRHistory cdr, AstWorkflowActionBean workflowbean) {
		Queue<AstCDRJob> astCDRJobQueue = getChannelVsAstCDRJobQueue().get(cdr.getChannel());
		if (astCDRJobQueue == null) {
			astCDRJobQueue = new LinkedList<AstCDRJob>();
			getChannelVsAstCDRJobQueue().put(cdr.getChannel(), astCDRJobQueue);

		}
		addJobToCDRJobQueue(astCDRJobQueue, cdr, workflowbean);

		AstCDRJob peakJob = astCDRJobQueue.peek();

		if (peakJob != null) {

			if (!peakJob.isJobInProgress()) {

				boolean isExecuted = peakJob.execute();
				if (!isExecuted) {
					getChannelVsAstCDRJobQueue().remove(cdr.getChannel());
				}
			}
		}
	}

	public void addNewJobAstCDRJobToChannelVsCDRActionQueue(AstCDRHistory cdr, AstWorkflowActionBean workflowBean) {
		addNewActionToChannelVsAstCDRJobQueue(cdr, workflowBean);
	}

	public Map<String, Queue<AstCDRJob>> getChannelVsAstCDRJobQueue() {
		return channelVsAstCDRJobQueue;
	}

	public void setChannelVsAstCDRJobQueue(Map<String, Queue<AstCDRJob>> channelVsAstCDRJobQueue) {
		this.channelVsAstCDRJobQueue = channelVsAstCDRJobQueue;
	}

	public List<String> getAllowedWorkflowActions() {
		return allowedWorkflowActions;
	}

	public void setAllowedWorkflowActions(List<String> allowedWorkflowActions) {
		this.allowedWorkflowActions = allowedWorkflowActions;
	}

}
