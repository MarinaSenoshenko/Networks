package controller.node;

import controller.msg.AnnounceMsg;
import controller.msg.Receiver;
import controller.msg.Sender;
import controller.node.info.GameTimers;
import controller.node.info.MasterInfo;
import exceptions.PacketSendException;
import lombok.Getter;
import lombok.Setter;
import model.Model;
import org.jetbrains.annotations.NotNull;
import proto.SnakesProto.GameMessage.*;
import proto.SnakesProto.*;

import javax.swing.table.DefaultTableModel;
import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static proto.SnakesProto.NodeRole.MASTER;

@Getter
@Setter
public class MsgHandler implements Closeable {

    protected int id;
    protected Model model;
    protected NodeRole role;
    protected MasterInfo masterInfo;
    protected Sender gameMsgSender;
    private Receiver gameMsgReceiver;
    private @NotNull AnnounceMsg announceMsgReceiver;
    private @NotNull DatagramSocket datagramSocket = new DatagramSocket();
    private GameTimers gameTimers = new GameTimers(new Timer(), new Timer(), new Timer(), new Timer());

    protected final Map<Integer, Long> lastMsgsFrom = new ConcurrentHashMap<>();
    protected final Map<Integer, Long> lastMsgsTo = new ConcurrentHashMap<>();
    protected final List<GameMessage> noAckMsgs = Collections.synchronizedList(new ArrayList<>());

    protected MsgHandler() throws SocketException {
    }

    public void startReceiveAnnouncement(DefaultTableModel table) throws IOException {
        announceMsgReceiver = new AnnounceMsg(table);
        announceMsgReceiver.getThread().start();
    }

    public void sendChangeRoleMessage(int receiverId, NodeRole receiverRole) throws UnknownHostException,
            PacketSendException {

        GamePlayer player = model.getPlayerById(receiverId);
        InetAddress address = InetAddress.getByName(player.getIpAddress());
        int port = player.getPort();
        gameMsgSender.sendRoleChangeMsg(id, receiverId, role, receiverRole, address, port);

    }

    public void sendSteerMsg(Direction direction) throws PacketSendException {
        if (role == MASTER) {
            SteerMsg steerMsg = SteerMsg.newBuilder().setDirection(direction).build();
            GameMessage gameMessage = GameMessage.newBuilder()
                    .setSteer(steerMsg)
                    .setSenderId(id)
                    .setMsgSeq(gameMsgSender.getNextMsgSeq())
                    .build();
            addSteer(id, gameMessage);
        } else {
            gameMsgSender.sendSteerMsg(id, masterInfo.getMasterId(), direction,
                    masterInfo.getMasterIp(), masterInfo.getMasterPort());
        }
    }

    public void addSteer(int id, GameMessage gameMessage) {
        model.addChangeDirection(id, gameMessage);
    }

    public void stopReceiveAnnouncement() {
        announceMsgReceiver.close();
    }

    public void stopPlaying() {
        if (role == MASTER) {
            gameTimers.getModelUpdater().cancel();
            gameTimers.getAnnounceSender().cancel();
        }
        gameMsgReceiver.getThread().interrupt();
        gameTimers.getAliveChecker().cancel();
        gameTimers.getMsgsResender().cancel();
    }

    @Override
    public void close() {
        datagramSocket.close();
        announceMsgReceiver.close();
        if (gameMsgReceiver != null) {
            gameMsgReceiver.getThread().interrupt();
        }

        gameTimers.getModelUpdater().cancel();
        gameTimers.getMsgsResender().cancel();
        gameTimers.getAnnounceSender().cancel();
        gameTimers.getAliveChecker().cancel();
    }
}
