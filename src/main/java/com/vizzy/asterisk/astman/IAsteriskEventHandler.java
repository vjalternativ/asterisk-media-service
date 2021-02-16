package com.vizzy.asterisk.astman;

import org.asteriskjava.manager.event.DialEndEvent;
import org.asteriskjava.manager.event.DtmfEvent;
import org.asteriskjava.manager.event.HangupEvent;
import org.asteriskjava.manager.event.NewChannelEvent;
import org.asteriskjava.manager.event.NewExtenEvent;

public interface IAsteriskEventHandler {
	void handleNewExtenEvent(NewExtenEvent event);

	void handleNewChannelEvent(NewChannelEvent event);

	void handleHangupEvent(HangupEvent event);

	void handleDtmfEvent(DtmfEvent event);

	void handleDialEndEvent(DialEndEvent event);
}
