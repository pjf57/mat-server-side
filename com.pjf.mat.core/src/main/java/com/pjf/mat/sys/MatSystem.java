package com.pjf.mat.sys;

import java.io.FileInputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.pjf.marketsim.EventFeed;
import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Element;
import com.pjf.mat.impl.MatInterface;

public class MatSystem {
	private final static Logger logger = Logger.getLogger(MatSystem.class);
	private final MatInterface mat;
	private EventFeed feed;
	
	public MatSystem(Properties props) throws SocketException, UnknownHostException {
		UDPComms comms = new UDPComms("192.168.0.9",2000);
		mat = new MatInterface(props,comms);
		comms.setMat(mat);
		feed = new EventFeed(comms.getCxn(),15000);
	}
	

	private void configureSingleEMA() throws Exception {
		Element tg1 = mat.getElement(2);
		Element ema1 = mat.getElement(3);
		Element l4ip1 = mat.getElement(6);
		Element lgr = mat.getElement(1);

		// configure element attributes
		tg1.getAttribute("len").setValue("60");
		tg1.getAttribute("gap").setValue("5");
		tg1.getAttribute("initial value").setValue("50");
		tg1.getAttribute("p1").setValue("0.25");
		ema1.getAttribute("len").setValue("7");
		ema1.getAttribute("alpha").setValue("0.25");
		l4ip1.getAttribute("oper").setValue("3044");	// Z = A > K1
		l4ip1.getAttribute("k1").setValue("55");

		// configure element connections
		ema1.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		l4ip1.getInputs().get(0).connectTo(ema1.getOutputs().get(0));
		lgr.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		lgr.getInputs().get(1).connectTo(ema1.getOutputs().get(0));
		lgr.getInputs().get(2).connectTo(l4ip1.getOutputs().get(0));
		logger.info("mat is: " + mat);

		mat.configureHW();
	}

	private void configureDualEMA() throws Exception {
		Element tg1 = mat.getElement(2);
		Element ema1 = mat.getElement(3);
		Element ema2 = mat.getElement(4);
		Element l4ip1 = mat.getElement(6);
		Element lgr = mat.getElement(1);

		// configure element attributes
		tg1.getAttribute("len").setValue("60");
		tg1.getAttribute("gap").setValue("5");
		tg1.getAttribute("initial value").setValue("50");
		tg1.getAttribute("p1").setValue("0.25");
		ema1.getAttribute("len").setValue("7");
		ema1.getAttribute("alpha").setValue("0.25");
		ema2.getAttribute("len").setValue("3");
		ema2.getAttribute("alpha").setValue("0.5");
//		l4ip1.getAttribute("oper").setValue("3004");	// Z = A > B
		l4ip1.getAttribute("oper").setValue("3044");	// Z = A > K1
		l4ip1.getAttribute("k1").setValue("55");

		// configure element connections
		ema1.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		ema2.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		l4ip1.getInputs().get(0).connectTo(ema1.getOutputs().get(0));
		lgr.getInputs().get(0).connectTo(l4ip1.getOutputs().get(0));
		lgr.getInputs().get(1).connectTo(ema2.getOutputs().get(0));
		lgr.getInputs().get(2).connectTo(tg1.getOutputs().get(0));
		lgr.getInputs().get(3).connectTo(ema1.getOutputs().get(0));
		logger.info("mat is: " + mat);

		mat.configureHW();
	}

	private void configureDualEMA_MFD() throws Exception {
		Element mfd = mat.getElement(7);
		Element ema1 = mat.getElement(3);
		Element ema2 = mat.getElement(4);
		Element ema3 = mat.getElement(5);
		Element l4ip1 = mat.getElement(6);
		Element lgr = mat.getElement(1);

		// configure element attributes
		mfd.getAttribute("udp listen port").setValue("15000");
		mfd.getAttribute("price_op").setValue("0");
		mfd.getAttribute("volume_op").setValue("f");
		mfd.getAttribute("mdtype").setValue("1");
		ema1.getAttribute("len").setValue("7");
		ema1.getAttribute("alpha").setValue("0.25");
		ema2.getAttribute("len").setValue("3");
		ema2.getAttribute("alpha").setValue("0.5");
		ema3.getAttribute("len").setValue("5");
		ema3.getAttribute("alpha").setValue("0.333333");
		l4ip1.getAttribute("oper").setValue("3004");	// Z = A > B
//		l4ip1.getAttribute("oper").setValue("3044");	// Z = A > K1
//		l4ip1.getAttribute("k1").setValue("19");

		// configure element connections
		ema1.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		ema2.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		ema3.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		l4ip1.getInputs().get(0).connectTo(ema1.getOutputs().get(0));
		l4ip1.getInputs().get(1).connectTo(ema2.getOutputs().get(0));
		l4ip1.getInputs().get(2).connectTo(ema3.getOutputs().get(0));
		l4ip1.getInputs().get(3).connectTo(mfd.getOutputs().get(0));
		lgr.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		lgr.getInputs().get(1).connectTo(ema1.getOutputs().get(0));
		lgr.getInputs().get(2).connectTo(ema2.getOutputs().get(0));
		lgr.getInputs().get(3).connectTo(l4ip1.getOutputs().get(0));
		logger.info("mat is: " + mat);

		mat.configureHW();
	}

	private void configureArith_MFD16() throws Exception {
		Element mfd = mat.getElement(15);
		Element ema_p = mat.getElement(3);
		Element ema_q = mat.getElement(4);
		Element ema_s = mat.getElement(5);
		Element l4ip1 = mat.getElement(9);
		Element a4ip_macd = mat.getElement(11);
		Element a4ip_hist = mat.getElement(12);
		Element lgr = mat.getElement(1);

		// configure element attributes
		mfd.getAttribute("udp listen port").setValue("15000");
		mfd.getAttribute("price_op").setValue("0");
		mfd.getAttribute("volume_op").setValue("f");
		mfd.getAttribute("mdtype").setValue("1");
		ema_p.getAttribute("len").setValue("7");
		ema_p.getAttribute("alpha").setValue("0.25");
		ema_q.getAttribute("len").setValue("3");
		ema_q.getAttribute("alpha").setValue("0.5");
		ema_s.getAttribute("len").setValue("5");
		ema_s.getAttribute("alpha").setValue("0.333333");
		a4ip_macd.getAttribute("oper").setValue("3000");	// Z = A - B
		a4ip_hist.getAttribute("oper").setValue("3000");	// Z = A - B

		// configure element connections
		ema_p.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		ema_q.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		a4ip_macd.getInputs().get(0).connectTo(ema_p.getOutputs().get(0));
		a4ip_macd.getInputs().get(1).connectTo(a4ip_macd.getOutputs().get(0));
		ema_s.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		a4ip_hist.getInputs().get(0).connectTo(a4ip_macd.getOutputs().get(0));
		a4ip_hist.getInputs().get(1).connectTo(ema_s.getOutputs().get(0));
		lgr.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		lgr.getInputs().get(1).connectTo(a4ip_macd.getOutputs().get(0));
		lgr.getInputs().get(2).connectTo(a4ip_hist.getOutputs().get(0));
		lgr.getInputs().get(3).connectTo(ema_s.getOutputs().get(0));
		logger.info("mat is: " + mat);

		mat.configureHW();
	}

	public void shutdown() {
		logger.info("Shutting down ...");
		mat.shutdown();
	}

	private void sendTradeBurst() throws Exception {
		feed.sendTradeBurst("resources/GLP_27667_1.csv",20,1,1);
	}

	private void sendCmd(int elId, String cmdName) {
		Element el = mat.getElement(elId);
		if (el != null) {
			Cmd cmd = el.getCmds().get(0);
			mat.sendCmd(cmd);
		}
	}
	
	private void reqStatus() {
		mat.getHWStatus();		
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Properties props;
		try {
			props = loadProperties();
			MatSystem sys = new MatSystem(props);
			logger.info("-----");	sys.reqStatus(); Thread.sleep(500);
			sys.configureArith_MFD16();
			logger.info("-----");	sys.reqStatus(); Thread.sleep(500);
//			sys.sendCmd(2,"start");
			sys.sendTradeBurst();
			logger.info("-----");	sys.reqStatus(); Thread.sleep(500);
			Thread.sleep(1000);
			sys.shutdown();
		} catch (Exception e) {
			logger.error("Outer error catcher: " + e.getMessage());
			e.printStackTrace();
		}		
	}






	private static Properties loadProperties() throws Exception {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream("resources/mat.properties"));
		} catch (Exception e) {
			throw new Exception("Cant load properties file",e);
		}
		return props;
	}

}
