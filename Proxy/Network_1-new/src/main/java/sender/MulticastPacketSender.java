package main.java.sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class MulticastPacketSender {
    private MulticastSocket multicastSocket;
    private DatagramPacket datagramPacket;

    public MulticastPacketSender(MulticastSocket multicastSocket, InetSocketAddress inetSocketAddress) {
        this.datagramPacket = new DatagramPacket("".getBytes(StandardCharsets.UTF_8), 0, inetSocketAddress);
        this.multicastSocket = multicastSocket;
    }

    public boolean sendPacket(String message) {
    	try {
    	    datagramPacket.setData(message.getBytes(StandardCharsets.UTF_8));
    	    datagramPacket.setLength(message.length());
            multicastSocket.send(datagramPacket);
            return true;
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return false;
    }
}
