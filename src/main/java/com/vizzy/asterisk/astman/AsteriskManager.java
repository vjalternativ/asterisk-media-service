package com.vizzy.asterisk.astman;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.DefaultManagerConnection;
import org.asteriskjava.manager.ManagerEventListener;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.RedirectAction;
import org.asteriskjava.manager.action.SetVarAction;
import org.asteriskjava.manager.event.ManagerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsteriskManager extends Thread implements ManagerEventListener {

	private DefaultManagerConnection managerConnection;
	private static Logger logger = LoggerFactory.getLogger(AsteriskManager.class);

	private Queue<ManagerEvent> eventQueue = new LinkedList<ManagerEvent>();

	Queue<ManagerEvent> getEventQueue() {
		return eventQueue;
	}

	void setEventQueue(Queue<ManagerEvent> eventQueue) {
		this.eventQueue = eventQueue;
	}

	public AsteriskManager(String host, String user, String secret)
			throws IllegalStateException, IOException, AuthenticationFailedException, TimeoutException {

		managerConnection = new DefaultManagerConnection(host, user, secret);
		managerConnection.setKeepAliveAfterAuthenticationFailure(true);
		managerConnection.addEventListener(this);
		managerConnection.login();
	}

	@Override
	public void onManagerEvent(ManagerEvent event) {

		logger.info("got new event from asterisk " + event.toString());
		eventQueue.add(event);
	}

	public void sendRedirectAction(String channel, String context, String exten, Integer priority)
			throws IllegalArgumentException, IllegalStateException, IOException, TimeoutException {
		RedirectAction redirectAction = new RedirectAction(channel, context, exten, priority);
		managerConnection.sendAction(redirectAction);
	}

	public void sendSetVarAction(String channel, String variable, String value)
			throws IllegalArgumentException, IllegalStateException, IOException, TimeoutException {
		SetVarAction setvarAction = new SetVarAction(channel, variable, value);
		managerConnection.sendAction(setvarAction);
	}

}
