package edu.harvard.econcs.turkserver.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;

import edu.harvard.econcs.turkserver.api.ExperimentLog;
import edu.harvard.econcs.turkserver.client.SessionClient;
import edu.harvard.econcs.turkserver.logging.FakeExperimentLog;

public class TestUtils {
	
	public static int PORT_SLEEP_MILLIS = 200;

	public static FakeHITWorkerGroup getFakeGroup(String prefix, int groupSize, Class<?> clientClass) throws Exception {		
		FakeHITWorkerGroup fakeGroup = new FakeHITWorkerGroup();
		
		for(int i = 1; i <= groupSize; i++ ) {									
			String hitId = "HIT " + prefix + i;
			String workerId = "Worker " + prefix + i;
			String assignmentId = "Assignment " + prefix +i;
			String username = "Username " + prefix +i;
			
			FakeHITWorker fake = FakeHITWorker.getNew(hitId, assignmentId, workerId, username, clientClass);			
			
			fakeGroup.addWorker(fake);
		}
		
		return fakeGroup;
	}
	
	public static FakeExperimentController getFakeController(FakeHITWorkerGroup fakeGroup) {
		FakeExperimentController fakeCont = new FakeExperimentController(fakeGroup);
		
		return fakeCont;
	}
	
	public static ExperimentLog getFakeLog() {
		return new FakeExperimentLog();
	}
	
	public static void disconnectAll(List<? extends SessionClient<?>> clients) {
		for( SessionClient<?> client : clients ) {
			client.disconnect();			
		}		
	}

	public static void waitForPort(int port) {
		while( !available(port) ) {
			try { Thread.sleep(PORT_SLEEP_MILLIS); } 
			catch (InterruptedException e) {}
		}
	}

	/**
	 * Stolen from http://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java
	 * @param port
	 * @return
	 */
	public static boolean available(int port) {
	    if (port < 1 || port > 65535 ) {
	        throw new IllegalArgumentException("Invalid start port: " + port);
	    }

	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }

	    return false;
	}
}
