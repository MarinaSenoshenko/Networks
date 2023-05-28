package main.java.reciever;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastPacketReceiver {
    private MulticastSocket multicastSocket;
    private DatagramPacket datagramPacket;
    private byte[] buffer;

    public MulticastPacketReceiver(MulticastSocket multicastSocket, int bufferSize) {
        this.buffer = new byte[bufferSize];
        this.datagramPacket = new DatagramPacket(buffer, bufferSize);
        this.multicastSocket = multicastSocket;
    }

    public String receivePacket() {
        try {
            multicastSocket.receive(datagramPacket);
            return new String(buffer);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return "";
    }    
}
