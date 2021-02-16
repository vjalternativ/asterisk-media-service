package com.vizzy.media.util;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class IDGenerator implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AtomicLong ID;

	public IDGenerator() {
		this(0);
	}

	public IDGenerator(long initialValue) {
		this.ID = new AtomicLong(initialValue);
	}

	private String getNextStringId() {
		return Long.toString(ID.getAndIncrement());
	}

	public String getId(String context) {
		return MessagingService.getInstance().getNodeId() + "-" + context + "-" + getNextStringId();
	}
}
