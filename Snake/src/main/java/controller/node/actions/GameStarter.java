package controller.node.actions;

import controller.msg.Receiver;
import controller.msg.Sender;
import controller.node.info.MasterInfo;
import controller.node.Node;
import exceptions.PacketSendException;
import model.Model;
import proto.SnakesProto.*;
import proto.SnakesProto.GameState.*;
import proto.SnakesProto.GameMessage.*;
import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static controller.msg.MsgConstants.*;
import static proto.SnakesProto.NodeRole.*;


public class GameStarter extends GameNode {
    private final TimerInitializer timerInitializer = new TimerInitializer();

    public GameStarter(Node node) {
        super(node);
    }

    public void startNewGame() {
        node.setRole(MASTER);
        GamePlayer player = node.getModel().getPlayerById(node.getId());
        player = player.toBuilder().setRole(MASTER).build();
        node.getModel().addPlayer(player);
        node.getGameTimers().setModelUpdater(new Timer());
        node.getGameTimers().setAnnounceSender(new Timer());

        node.getGameTimers().getModelUpdater().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                node.getModel().createGameField();
                node.setState(node.getModel().getState());
                GamePlayers players = node.getState().getPlayers();
                for (int i = 0; i < players.getPlayersCount(); i++) {
                    GamePlayer player = players.getPlayers(i);
                    if (player.getRole() != MASTER) {
                        try {
                            InetAddress address = InetAddress.getByName(players.getPlayers(i).getIpAddress());
                            node.getGameMsgSender().sendStateMsg(node.getId(), player.getId(),
                                    node.getState(), address, players.getPlayers(i).getPort());
                        } catch (IOException ignored) {

                        }
                    }
                }
            }
        }, 0, node.getState().getConfig().getStateDelayMs());

        node.getGameTimers().getAnnounceSender().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    node.getGameMsgSender().sendAnnounceMsg(node.getState());
                } catch (PacketSendException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, ANNOUNCEMENNT_PERIOD);

        timerInitializer.initMessageResender();
        timerInitializer.initAliveChecker();
    }

    public void startExistingGame() {
        node.setGameMsgReceiver(new Receiver(1, node.getDatagramSocket(), node,
                node.getGameMsgSender(), node.getLastMsgsFrom()));
        node.getGameMsgReceiver().getThread().start();
        timerInitializer.initMessageResender();
        timerInitializer.initAliveChecker();
    }

    public void createNewGame() throws IOException {
        node.setId(1);
        String name = node.getProperties().getProperty("name");
        GamePlayer player = node.createGamePlayer(node.getId(), MASTER, "",
                node.getDatagramSocket().getLocalPort(), name);
        Snake snake = node.getSnakeCreater().createSnake(1,
                node.getSnakeCreater().createCoord(2, 2));
        GamePlayers players = GamePlayers.newBuilder().addPlayers(player).build();
        node.setState(GameState.newBuilder()
                .setStateOrder(0)
                .setConfig(node.getConfig())
                .setPlayers(players)
                .addSnakes(snake)
                .build());
        node.setModel(new Model(node.getConfig(), node));
        node.getModel().modelFromState(node.getState());
        try {
            node.setGameMsgSender(new Sender(0, InetAddress.getByName(MULTICAST_IP),
                    node.getDatagramSocket(), node.getNoAckMsgs(), node.getLastMsgsTo(),
                    node.getModel()));
            node.setGameMsgReceiver(new Receiver(1, node.getDatagramSocket(), node,
                    node.getGameMsgSender(), node.getLastMsgsTo()));
            node.getGameMsgReceiver().getThread().start();
            startNewGame();
        } catch (UnknownHostException e) {
            throw new RuntimeException(GameStarter.class + e.getLocalizedMessage());
        }
    }

    public void joinGame(boolean isViewer, String info)  {
        try {
            node.setState(null);
            node.setModel(new Model(node.getConfig(), node));
            node.setGameMsgSender(new Sender(0, InetAddress.getByName(MULTICAST_IP),
                    node.getDatagramSocket(), node.getNoAckMsgs(), node.getLastMsgsTo(),
                    node.getModel()));

            byte[] buffer = new byte[BUFFER_MSG_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            String[] infoTokens = info.split(":");
            int port = Integer.parseInt(infoTokens[2]);
            InetAddress address = InetAddress.getByName(infoTokens[1].substring(1));

            String name = node.getProperties().getProperty("name");
            node.getGameMsgSender().sendJoinMsg(1, name, isViewer, address, port);

            while (true) {
                node.getDatagramSocket().receive(packet);
                GameMessage gameMessage = GameMessage.parseFrom(Arrays.copyOf(packet.getData(),
                        packet.getLength()));
                node.getNoAckMsgs().clear();
                if (gameMessage.hasError()) {
                    ErrorMsg errorMsg = gameMessage.getError();
                    JOptionPane.showMessageDialog(null, errorMsg.getErrorMessage(),
                            "Error in message", JOptionPane.ERROR_MESSAGE);
                } else if (gameMessage.hasAck()) {
                    if (gameMessage.getMsgSeq() == 1) {
                        node.setId(gameMessage.getReceiverId());
                        node.setMasterInfo(new MasterInfo(gameMessage.getSenderId(), packet.getPort(),
                                packet.getAddress()));
                        if (!isViewer) {
                            node.setRole(NORMAL);
                        } else {
                            node.setRole(VIEWER);
                        }
                        startExistingGame();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(GameStarter.class + e.getLocalizedMessage());
        }
    }
    private class TimerInitializer {
        private void initMessageResender() {
            node.getGameTimers().setMsgsResender(new Timer());
            int ping_delay_ms = node.getConfig().getPingDelayMs();
            node.getGameTimers().getMsgsResender().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    node.getGameMsgSender().sendNoAckMsg();
                }
            }, 0, ping_delay_ms);
        }

        private void initAliveChecker() {
            node.getGameTimers().setAliveChecker(new Timer());
            int pingDelayMs = node.getConfig().getPingDelayMs();
            int nodeTimeoutMs = node.getConfig().getNodeTimeoutMs();

            node.getGameTimers().getAliveChecker().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    node.getLastMsgsFrom().keySet().removeIf(n -> {
                        if (System.currentTimeMillis() - node.getLastMsgsFrom().get(n) > nodeTimeoutMs) {
                            GamePlayer player = node.getModel().getPlayerById(n);
                            if (player != null) {
                                if (node.getRole() == MASTER) {
                                    node.getModel().removePlayer(n);
                                    if (player.getRole() == DEPUTY) {
                                        node.findNewDeputy();
                                    }
                                } else if (node.getRole() != DEPUTY) {
                                    if (player.getRole() == MASTER) {
                                        node.makeDeputyMaster();
                                    }
                                } else {
                                    node.getStarter().startNewGame();
                                }
                                return true;
                            }
                        }
                        return false;
                    });
                    node.getGameMsgSender().sendPingMsg(node.getId(), pingDelayMs);
                }
            }, 0, pingDelayMs);
        }
    }
}
