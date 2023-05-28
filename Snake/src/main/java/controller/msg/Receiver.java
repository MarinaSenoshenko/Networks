package controller.msg;

import controller.node.Node;
import exceptions.PacketReceiveException;
import lombok.Getter;
import proto.SnakesProto.GameMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.Map;

import static controller.msg.MsgConstants.BUFFER_MSG_SIZE;

@Getter
public class Receiver extends MsgProcessor {

    public Receiver(int playersId, DatagramSocket datagramSocket, Node node, Sender sender,
                    Map<Integer, Long> lastMessagesFrom) {
        super(playersId, datagramSocket, node, sender, lastMessagesFrom);
    }

    @Override
    public void startReceive() throws PacketReceiveException {
        byte[] buffer = new byte[BUFFER_MSG_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
        try {
            while (!thread.isInterrupted()) {
                datagramSocket.receive(packet);
                GameMessage gameMsg = GameMessage.parseFrom(Arrays.copyOf(packet.getData(), packet.getLength()));
                if (!gameMsg.hasJoin()) {
                    lastMsgFrom.put(gameMsg.getSenderId(), System.currentTimeMillis());
                }
                if (gameMsg.hasSteer()) {
                    steerMsg(gameMsg, packet.getAddress(), packet.getPort());
                } else if (gameMsg.hasAck()) {
                    ackMsg(gameMsg);
                } else if (gameMsg.hasState()) {
                    stateMsg(gameMsg, packet.getAddress(), packet.getPort());
                } else if (gameMsg.hasJoin()) {
                    joinMsg(gameMsg, packet.getAddress(), packet.getPort());
                } else if (gameMsg.hasPing()) {
                    pingMsg(gameMsg, packet.getAddress(), packet.getPort());
                } else if (gameMsg.hasRoleChange()) {
                    roleChangeMsg(gameMsg, packet.getAddress(), packet.getPort());
                }
            }
        } catch (IOException e) {
            throw new PacketReceiveException(AnnounceMsg.class + e.getMessage());
        }
    }
}
