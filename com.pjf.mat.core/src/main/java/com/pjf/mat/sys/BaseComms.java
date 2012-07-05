package com.pjf.mat.sys;

import java.util.Collection;

import com.pjf.mat.api.Comms;
import com.pjf.mat.api.NotificationCallback;

public abstract class BaseComms implements Comms {
	protected Collection<NotificationCallback> notificationSubscribers;

	@Override
	public void addNotificationSubscriber(NotificationCallback subscriber) {
		notificationSubscribers.add(subscriber);		
	}

}
