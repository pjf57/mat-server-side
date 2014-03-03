package com.pjf.mat.api;


import java.io.IOException;
import java.util.Collection;

import com.pjf.mat.api.util.HwStatus;



public interface Comms {
	public void sendConfig(Collection<Element> collection) throws Exception;
	public Status requestStatus() throws Exception;
	public Status requestStatus(Element element) throws Exception;
	public void requestLkuAuditLogs() throws Exception;
	public void requestRtrAuditLogs() throws Exception;
	public void sendCmd(Cmd cmd) throws Exception;
	public void shutdown();
	public void addNotificationSubscriber(NotificationCallback subscriber);
	public long getHWSignature() throws Exception;
	public void setHwStatus(HwStatus st);
	public HwStatus getHWStatus();
	public void synchroniseClock(long syncOriginMs) throws Exception;
	public void subscribeIncomingMsgs(int port, InMsgCallbackInt cb);
	public void resetCounters() throws IOException;
}
