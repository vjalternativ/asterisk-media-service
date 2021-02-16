package com.vizzy.asterisk.astman;

public class AstCDRHistoryService {
	private static AstCDRHistoryService instance;

	private AstCDRHistoryService() {
	}

	static AstCDRHistoryService getInstance() {
		if (instance == null) {
			instance = new AstCDRHistoryService();
		}
		return instance;

	}

}
