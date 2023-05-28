package main.java.core;

import main.java.sender.MulticastPacketSender;

public class ThreadNotifier implements Runnable {
    private MulticastPacketSender multicastPacketSender;
    private String message;
    private int notifyPeriod;
    private Thread thread = new Thread(this);

    public ThreadNotifier(MulticastPacketSender multicastPacketSender, String message, int notifyPeriod) {
        this.multicastPacketSender = multicastPacketSender;
        this.message = message;
        this.notifyPeriod = notifyPeriod;
    }
    
    public Thread getThread() {
    	return thread;
    }

    private void notifyGroup() {
        try {
	    while (!Thread.interrupted()) {		    
	        Thread.sleep(notifyPeriod);
	        multicastPacketSender.sendPacket(message);			    
            }
        } catch (InterruptedException e) {
	      e.printStackTrace();
	}
    }   

    @Override
    public void run() {
	  notifyGroup();
    }
}
