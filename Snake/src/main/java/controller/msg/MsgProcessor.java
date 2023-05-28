package controller.msg;

import controller.node.Node;
import exceptions.PacketSendException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import proto.SnakesProto.*;
import proto.SnakesProto.GameMessage.*;
import proto.SnakesProto.GameState.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

import static controller.msg.MsgConstants.UNKNOWN_SQUARE;
import static proto.SnakesProto.NodeRole.*;

@Getter
@Setter
@AllArgsConstructor
public abstract class MsgProcessor implements MsgReceiver {
    protected int playersId;
    protected DatagramSocket datagramSocket;
    protected Node node;
    protected Sender sender;
    protected Map<Integer, Long> lastMsgFrom;
    protected final Thread thread = new Thread(this);

    protected void pingMsg(GameMessage gameMsg, InetAddress address, int port) throws PacketSendException {
        sender.sendAckMsg(node.getId(), gameMsg.getSenderId(), gameMsg.getMsgSeq(), address, port);
    }

    protected void steerMsg(GameMessage gameMsg, InetAddress address, int port) throws PacketSendException {
        node.addSteer(gameMsg.getSenderId(), gameMsg);
        sender.sendAckMsg(node.getId(), gameMsg.getSenderId(), gameMsg.getMsgSeq(), address, port);
    }

    protected void ackMsg(GameMessage gameMsg) {
        sender.deleteMsgByMsgSeq(gameMsg.getMsgSeq());
    }

    protected void stateMsg(GameMessage gameMsg, InetAddress address, int port) throws PacketSendException {
        StateMsg stateMsg = gameMsg.getState();
        GameState state = stateMsg.getState();
        node.updateModelFromState(state);
        sender.sendAckMsg(node.getId(), gameMsg.getSenderId(), gameMsg.getMsgSeq(), address, port);
    }

    protected void joinMsg(GameMessage gameMsg, InetAddress address, int port) throws PacketSendException {
        JoinMsg joinMsg = gameMsg.getJoin();
        String addressString = address.toString().substring(1);
        playersId++;
        if (!joinMsg.getOnlyView()) {
            GamePlayer player = node.createGamePlayer(playersId, NORMAL, addressString, port, joinMsg.getName());
            Coord headCoord = node.getSnakeCreater().findHeadCoord();
            if (headCoord.getX() == UNKNOWN_SQUARE) {
                sender.sendErrorMsg(node.getId(), playersId, "Unknown square", address, port);
            } else {
                Snake snake = node.getSnakeCreater().createSnake(playersId, headCoord);
                node.getModel().addPlayer(player);
                node.getModel().addSnake(snake);
                sender.sendAckMsg(node.getId(), playersId, gameMsg.getMsgSeq(), address, port);
            }
        } else {
            GamePlayer player = node.createGamePlayer(playersId, VIEWER, addressString, port,
                    joinMsg.getName());
            node.getModel().addPlayer(player);
            sender.sendAckMsg(node.getId(), playersId, gameMsg.getMsgSeq(), address, port);
        }
        lastMsgFrom.put(playersId, System.currentTimeMillis());
        node.findNewDeputy();
    }

    protected void roleChangeMsg(GameMessage gameMsg, InetAddress address, int port) throws IOException {
        RoleChangeMsg roleChangeMsg = gameMsg.getRoleChange();
        NodeRole prevRole = node.getRole();
        NodeRole newRole = roleChangeMsg.getReceiverRole();
        node.setRole(roleChangeMsg.getReceiverRole());
        if (prevRole == DEPUTY && newRole == MASTER) {
            node.findNewDeputy();
            node.getStarter().startNewGame();
        } else if (newRole == VIEWER) {
            node.stopPlaying();
            node.getStarter().startExistingGame();
        }
        sender.sendAckMsg(node.getId(), gameMsg.getSenderId(), gameMsg.getMsgSeq(), address, port);
    }
}
