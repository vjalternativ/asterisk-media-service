package com.vizzy.asterisk.astman.bean;

import java.util.Date;

public class AstCDRHistory {
	private String channel;
	private String callerIdName;
	private String callerIdNum;
	private String exten;
	private Date startTime;
	private Date endTime;
	private String id;
	private String hangupCause;
	private Integer hangupCauseCode;
	private String context;
	private String dialStatus;
	private String uniqueId;
	private String linkedId;

	public AstCDRHistory() {
	}

	public String getExten() {
		return exten;
	}

	public void setExten(String exten) {
		this.exten = exten;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getCallerIdName() {
		return callerIdName;
	}

	public void setCallerIdName(String callerIdName) {
		this.callerIdName = callerIdName;
	}

	public String getCallerIdNum() {
		return callerIdNum;
	}

	public void setCallerIdNum(String callerIdNum) {
		this.callerIdNum = callerIdNum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHangupCause() {
		return hangupCause;
	}

	public void setHangupCause(String hangupCause) {
		this.hangupCause = hangupCause;
	}

	public Integer getHangupCauseCode() {
		return hangupCauseCode;
	}

	public void setHangupCauseCode(Integer hangupCauseCode) {
		this.hangupCauseCode = hangupCauseCode;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getDialStatus() {
		return dialStatus;
	}

	public void setDialStatus(String dialStatus) {
		this.dialStatus = dialStatus;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getLinkedId() {
		return linkedId;
	}

	public void setLinkedId(String linkedId) {
		this.linkedId = linkedId;
	}
}
