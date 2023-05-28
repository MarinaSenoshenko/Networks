package controller.msg;

import exceptions.PacketSendException;
import lombok.AllArgsConstructor;
import model.Model;
import proto.SnakesProto.*;
import proto.SnakesProto.GameMessage.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import static controller.msg.MsgConstants.MULTICAST_PORT;

@AllArgsConstructor
public class Sender {

    private int msgSeq;
    private InetAddress group;
    private DatagramSocket datagramSocket;
    private List<GameMessage> noAckMsg;
    private Map<Integer, Long> lastMsgTo;
    private Model model;

    public synchronized void sendNoAckMsg() {
        noAckMsg.removeIf(n -> {
            if (model.isPlayerExists(n.getReceiverId())) {
                try {
                    GamePlayer player = model.getPlayerById(n.getReceiverId());
                    if (player != null) {
                        InetAddress address = InetAddress.getByName(player.getIpAddress());
                        int port = player.getPort();
                        sendGameMsg(true, n, address, port);
                        return false;
                    }
                    return true;
                } catch (IOException ex) {
                    return true;
                }
            } else {
                return true;
            }
        });
    }

    public synchronized void sendPingMsg(int senderId, int pingDelayMs) {
        lastMsgTo.keySet().removeIf(n -> {
                if (System.currentTimeMillis() - lastMsgTo.get(n) > pingDelayMs) {
                    if (model.isPlayerExists(n)) {
                        try {
                            GamePlayer player = model.getPlayerById(n);
                            if (player != null) {
                                InetAddress address = InetAddress.getByName(player.getIpAddress());
                                int port = player.getPort();
                                sendPingMsg(senderId, n, address, port);
                                return false;
                            }
                            return true;
                        } catch (IOException ex) {
                            return true;
                        }
                    }
                    return true;
                }
                return false;
        });
    }

    public synchronized void deleteMsgByMsgSeq(long msgSeq) {
        noAckMsg.removeIf(n -> n.getMsgSeq() == msgSeq);
    }

    public synchronized void sendSteerMsg(int senderId, int receiverId, Direction direction,
                                          InetAddress masterIp, int masterPort) throws PacketSendException {
        msgSeq++;
        SteerMsg steerMsg = SteerMsg.newBuilder().setDirection(direction).build();
        GameMessage gameMsg = GameMessage.newBuilder()
                .setSteer(steerMsg)
                .setSenderId(senderId)
                .setReceiverId(receiverId)
                .setMsgSeq(msgSeq)
                .build();

        sendGameMsg(false, gameMsg, masterIp, masterPort);
    }

    public synchronized void sendStateMsg(int senderId, int receiverId, GameState state,
                                          InetAddress address, int port) throws PacketSendException {
        msgSeq++;
        StateMsg stateMsg = StateMsg.newBuilder().setState(state).build();
        GameMessage gameMsg = GameMessage.newBuilder()
                .setState(stateMsg)
                .setSenderId(senderId)
                .setReceiverId(receiverId)
                .setMsgSeq(msgSeq)
                .build();

        sendGameMsg(false, gameMsg, address, port);
    }

    public synchronized void sendAnnounceMsg(GameState state) throws PacketSendException {
        msgSeq++;
        GamePlayers players = state.getPlayers();
        GameConfig config = state.getConfig();
        AnnouncementMsg announcementMsg = AnnouncementMsg.newBuilder()
                .setConfig(config)
                .setPlayers(players)
                .build();
        GameMessage gameMsg = GameMessage.newBuilder()
                .setMsgSeq(msgSeq)
                .setAnnouncement(announcementMsg)
                .build();
        sendGameMsg(false, gameMsg, group, MULTICAST_PORT);
    }

    public synchronized void sendPingMsg(int senderId, int receiverId,
                                         InetAddress address, int port) throws PacketSendException {
        msgSeq++;
        PingMsg pingMsg = PingMsg.newBuilder().build();
        GameMessage gameMsg = GameMessage.newBuilder()
                .setPing(pingMsg)
                .setSenderId(senderId)
                .setReceiverId(receiverId)
                .setMsgSeq(msgSeq)
                .build();
        sendGameMsg(false, gameMsg, address, port);
    }


    public synchronized void sendGameMsg(boolean isResend, GameMessage gameMsg,
                                             InetAddress address, int port) throws PacketSendException {
        byte[] gameMsgBytes = gameMsg.toByteArray();
        DatagramPacket packet = new DatagramPacket(gameMsgBytes, gameMsgBytes.length, address, port);
        if (!gameMsg.hasAnnouncement() && !gameMsg.hasAck()) {
            if (!isResend) {
                noAckMsg.add(gameMsg);
            }
            lastMsgTo.put(gameMsg.getReceiverId(), System.currentTimeMillis());
        }
        try {
            datagramSocket.send(packet);
        } catch (IOException e) {
            throw new PacketSendException(AnnounceMsg.class + e.getMessage());
        }
    }

    public synchronized void sendJoinMsg(int receiverId, String name, boolean isViewer, InetAddress address,
                                             int port) throws PacketSendException {
        msgSeq++;
        JoinMsg joinMsg = JoinMsg.newBuilder().setName(name).setOnlyView(isViewer).build();
        GameMessage gameMsg = GameMessage.newBuilder()
                .setJoin(joinMsg)
                .setReceiverId(receiverId)
                .setMsgSeq(msgSeq)
                .build();
        sendGameMsg(false, gameMsg, address, port);
    }

    public synchronized void sendAckMsg(int senderId, int receiverId, long msgSeq, InetAddress address,
                                        int port) throws PacketSendException {
        AckMsg ackMsg = AckMsg.newBuilder().build();
        GameMessage gameMsg = GameMessage.newBuilder()
                .setAck(ackMsg)
                .setMsgSeq(msgSeq)
                .setSenderId(senderId)
                .setReceiverId(receiverId)
                .build();
        sendGameMsg(false, gameMsg, address, port);
    }

    public synchronized void sendErrorMsg(int senderId, int receiverId, String message,
                                          InetAddress address, int port) throws PacketSendException {
        msgSeq++;
        ErrorMsg errorMsg = ErrorMsg.newBuilder().setErrorMessage(message).build();
        GameMessage gameMsg = GameMessage.newBuilder()
                .setError(errorMsg)
                .setSenderId(senderId)
                .setReceiverId(receiverId)
                .setMsgSeq(msgSeq)
                .build();
        sendGameMsg(false, gameMsg, address, port);
    }

    public synchronized void sendRoleChangeMsg(int senderId, int receiverId, NodeRole senderRole, NodeRole
            receiverRole, InetAddress address, int port) throws PacketSendException {
        msgSeq++;
        RoleChangeMsg roleChangeMsg = RoleChangeMsg.newBuilder()
                .setReceiverRole(receiverRole)
                .setSenderRole(senderRole)
                .build();
        GameMessage gameMsg = GameMessage.newBuilder()
                .setRoleChange(roleChangeMsg)
                .setMsgSeq(msgSeq)
                .setReceiverId(receiverId)
                .setSenderId(senderId)
                .build();
        sendGameMsg(false, gameMsg, address, port);
    }

    public synchronized long getNextMsgSeq() {
        return ++msgSeq;
    }
}
