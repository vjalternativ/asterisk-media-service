package com.vizzy.asterisk.astman;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.asteriskjava.manager.event.DialEndEvent;
import org.asteriskjava.manager.event.DtmfEvent;
import org.asteriskjava.manager.event.HangupEvent;
import org.asteriskjava.manager.event.NewChannelEvent;
import org.asteriskjava.manager.event.NewExtenEvent;

import com.vizzy.ast.media.service.ICallHandler;
import com.vizzy.asterisk.astman.bean.AstCDRHistory;
import com.vizzy.media.util.IDGenerator;

public class AsteriskEventHandler extends Thread implements IAsteriskEventHandler {
	private static IDGenerator astcdrhistoryIdGenerator = new IDGenerator(0);

	private Map<String, AstCDRHistory> astChannelVsCDR = new HashMap<String, AstCDRHistory>();

	private AsteriskManager manager;
	private ICallHandler callHandlerService;
	private AsteriskEventManager asteriskEventManager;

	public AsteriskEventHandler(AsteriskManager manager, AsteriskEventManager asteriskEventManager,
			ICallHandler inboundcallHandler) {
		this.callHandlerService = inboundcallHandler;
		this.asteriskEventManager = asteriskEventManager;
		setManager(manager);
	}

	public void handleNewExtenEvent(NewExtenEvent event) {
		AstCDRHistory cdr = getAstChannelVsCDR().get(event.getChannel());
		if (cdr != null) {
			cdr.setExten(event.getExten());
			callHandlerService.handleNewExtenEvent(cdr, asteriskEventManager);
		}
	}

	public void handleNewChannelEvent(NewChannelEvent event) {
		String id = astcdrhistoryIdGenerator.getId("astcdr");
		AstCDRHistory cdr = new AstCDRHistory();
		cdr.setId(id);
		cdr.setCallerIdName(event.getCallerIdName());
		cdr.setCallerIdNum(event.getCallerIdNum());
		cdr.setChannel(event.getChannel());
		cdr.setExten(event.getExten());
		cdr.setStartTime(new Date());
		cdr.setContext(event.getContext());
		cdr.setUniqueId(event.getUniqueId());
		cdr.setLinkedId(event.getLinkedid());
		getAstChannelVsCDR().put(event.getChannel(), cdr);

		callHandlerService.handleNewChannel(cdr, asteriskEventManager);

	}

	public void handleHangupEvent(HangupEvent event) {
		AstCDRHistory cdr = getAstChannelVsCDR().get(event.getChannel());
		if (cdr != null) {
			cdr.setHangupCause(event.getCauseTxt());
			cdr.setHangupCauseCode(event.getCause());
			cdr.setEndTime(new Date());
			callHandlerService.handleChannelHangup(cdr, asteriskEventManager);
		}
	}

	private Map<String, AstCDRHistory> getAstChannelVsCDR() {
		return astChannelVsCDR;
	}

	public AsteriskManager getManager() {
		return manager;
	}

	public void setManager(AsteriskManager manager) {
		this.manager = manager;
	}

	@Override
	public void handleDtmfEvent(DtmfEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDialEndEvent(DialEndEvent event) {
		AstCDRHistory cdr = getAstChannelVsCDR().get(event.getChannel());
		if (cdr != null) {
			cdr.setDialStatus(event.getDialStatus());
		}
	}

}
