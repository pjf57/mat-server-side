package com.pjf.mat.sys;

import java.util.Collection;
import org.apache.log4j.Logger;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Comms;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.Status;

public class DummyComms implements Comms {
	
	private final static Logger logger = Logger.getLogger(DummyComms.class);

	@Override
	public void sendConfig(Collection<Element> elements) {
		logger.info("Configuring " + elements.size() + " elements.");
	}

	@Override
	public Status requestStatus() {
		logger.info("Requesting status all.");
		return null;
	}

	@Override
	public void sendCmd(Cmd cmd) {
		logger.info("Sending cmd: " + cmd);
	}

	@Override
	public void shutdown() {
		logger.info("Shutting down.");
	}

	@Override
	public Status requestStatus(Element element) throws Exception {
		logger.info("Requesting status for " + element);
		return null;
	}

}
