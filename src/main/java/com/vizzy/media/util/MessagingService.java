package com.vizzy.media.util;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MessagingService {
	private String nodeId;
	private static Logger logger = LoggerFactory.getLogger(MessagingService.class);

	private static MessagingService instance;

	private MessagingService() {
		getNodeId();
	}

	static MessagingService getInstance() {
		if (instance == null) {
			instance = new MessagingService();
		}
		return instance;

	}

	public String getNodeId() {
		if (nodeId != null) {
			return nodeId;
		}
		try {
			synchronized (this) {
				if (nodeId == null) {
					int rand = new Random().nextInt(900) + 100;
					long timestamp = System.currentTimeMillis() / 1000;
					nodeId = "v" + rand + "-" + Long.toHexString(timestamp);
				}
			}
		} catch (Exception e) {
			logger.error("Could not generate Node Id ... ", e); //$NON-NLS-1$
		}
		return nodeId;
	}

}
