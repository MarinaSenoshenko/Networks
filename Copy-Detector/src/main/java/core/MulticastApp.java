package main.java.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import main.java.context.Context;
import main.java.reciever.MulticastPacketReceiver;
import main.java.sender.MulticastPacketSender;

public class MulticastApp implements Runnable{
    private ThreadNotifier threadNotifier;
    private MulticastPacketReceiver multicastPacketReceiver;
    private UUID appId;
    private HashMap<String, Boolean> appCopies;
    private Timer timer;
    private Thread thread = new Thread(this);
    private MulticastSocket multicastSocket;
    
    private static final int TIMER_DELAY = 0;
    private static final int UPDATE_TIME = 2000;
    
    public MulticastApp(Context context) {
	timer = new Timer();
        appId = UUID.randomUUID();
        appCopies = new HashMap<>();
        threadNotifier = getMulticastNotifier(context, appId.toString(), context.getNotifyPeriod()); 
        multicastPacketReceiver = getMulticastReceiver(context, appId.toString().length());
        runApp();
    }
    
    private InetSocketAddress getGroupAddress(Context context) {
        try {
            return new InetSocketAddress(InetAddress.getByName(context.getIpGroup()), context.getPort()); // ip сокета - комбинация ip группы и порта
        } catch (UnknownHostException exc) {
            exc.printStackTrace();
        }
        return null;
    }

    private MulticastSocket getMulticastSocket(Context context, InetSocketAddress inetSocketAddress) {
        try {
            if (multicastSocket == null) {
                multicastSocket = new MulticastSocket(context.getPort()); 
                multicastSocket.joinGroup(inetSocketAddress, null);
            }
            return multicastSocket;
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return null;
    }

    private ThreadNotifier getMulticastNotifier(Context context, String message, int notifyPeriod) {
        return new ThreadNotifier(getMulticastPacketSender(context), message, notifyPeriod);
    }
    
    private MulticastPacketSender getMulticastPacketSender(Context context) { 
        return new MulticastPacketSender(getMulticastSocket(context, getGroupAddress(context)), getGroupAddress(context));
    }

    private MulticastPacketReceiver getMulticastReceiver(Context context, int bufferSize) { 
        return new MulticastPacketReceiver(getMulticastSocket(context, getGroupAddress(context)), bufferSize);
    }

    private boolean checkAppId(String id) {
    	try {
            if (!UUID.fromString(id).equals(appId)) {
                return true;
            }
        } catch (IllegalArgumentException exc) {
            exc.printStackTrace();
        }
    	return false;
    }
    
    private void printCopies() {
    	System.out.println("My app id:\n" + appId + "\nId of copies:"); 
    }

    private void showAllAppCopies() {
    	if (!(appCopies.values().removeAll(Collections.singleton(false)))) {  		
            appCopies.keySet().forEach(id -> appCopies.put(id, false));
            return;
        }
    	printCopies();
        appCopies.keySet().forEach(id -> {
            appCopies.put(id, false);
            System.out.println(id);
        });
    }

    private void runTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                showAllAppCopies();
            }
        }, TIMER_DELAY, UPDATE_TIME);
    }

    private void startFind() {
    	printCopies(); 
        while(!Thread.interrupted()) {
            String receiveAppId = multicastPacketReceiver.receivePacket();
            if (checkAppId(receiveAppId)) {
                if (!appCopies.containsKey(receiveAppId)) {
                    System.out.println(receiveAppId);
                }
                appCopies.put(receiveAppId, true);
            }
        }
    }

    private void runApp() {
        runTimer();

        threadNotifier.getThread().start();
		thread.start(); 
        
        try (Scanner scanner = new Scanner(System.in)) {
		String word = scanner.nextLine();
		if ("exit".equals(word)) {
			thread.interrupt();
			thread.join(); 
			threadNotifier.getThread().stop();
			threadNotifier.getThread().interrupt();
			threadNotifier.getThread().join();    
			multicastSocket.close();
			System.exit(0);
		}
		
     
        } catch (InterruptedException e) {
	     e.printStackTrace();
	}
        
    }

	@Override
	public void run() {
	     startFind(); 		
	}
}
