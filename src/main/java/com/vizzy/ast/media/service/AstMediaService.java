package com.vizzy.ast.media.service;

import java.io.IOException;
import java.util.List;

import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.TimeoutException;

import com.vizzy.asterisk.astman.AsteriskEventManager;
import com.vizzy.asterisk.astman.AsteriskManager;

public class AstMediaService {
	public AstMediaService(String host, String user, String secret, List<String> allowedWorkflowActions,
			ICallHandler callHandler)
			throws IllegalStateException, IOException, AuthenticationFailedException, TimeoutException {

		AsteriskManager asteriskManager = new AsteriskManager(host, user, secret);
		asteriskManager.start();

		AsteriskEventManager defaultEventManager = new AsteriskEventManager(asteriskManager, allowedWorkflowActions,
				callHandler);
		defaultEventManager.start();

	}
}
