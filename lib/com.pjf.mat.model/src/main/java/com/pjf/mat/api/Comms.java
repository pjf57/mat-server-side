package com.pjf.mat.api;


import java.util.Collection;



public interface Comms {
	public void sendConfig(Collection<Element> collection) throws Exception;
	public Status requestStatus() throws Exception;
	public Status requestStatus(Element element) throws Exception;
	public void sendCmd(Cmd cmd) throws Exception;
	public void shutdown();
	public void addNotificationSubscriber(NotificationCallback subscriber);
	public long getHWSignature() throws Exception;
	public void synchroniseClock(int syncOrigin) throws Exception;
}
