package com.vizzy.ast.media.service;

import com.vizzy.asterisk.astman.AsteriskEventManager;
import com.vizzy.asterisk.astman.bean.AstCDRHistory;

public interface ICallHandler {

	void handleNewChannel(AstCDRHistory cdr, AsteriskEventManager asteriskEventManager);

	void handleChannelHangup(AstCDRHistory cdr, AsteriskEventManager asteriskEventManager);

	void handleNewExtenEvent(AstCDRHistory cdr, AsteriskEventManager asteriskEventManager);

}
