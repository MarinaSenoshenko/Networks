package controller.msg;

import exceptions.PacketReceiveException;
import lombok.Getter;
import proto.SnakesProto.*;
import proto.SnakesProto.GameMessage.AnnouncementMsg;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.DefaultTableModel;
import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;

import static controller.msg.MsgConstants.*;
import static proto.SnakesProto.NodeRole.MASTER;

@Getter
public class AnnounceMsg implements MsgReceiver, Closeable {
    private DefaultTableModel table;
    private final @NotNull Timer timer = new Timer();
    private final HashMap<Vector<String>, Long> lastMsg = new HashMap<>();
    private final @NotNull MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
    private final Thread thread = new Thread(this);

    public AnnounceMsg(DefaultTableModel table) throws IOException {
        try {
            multicastSocket.joinGroup(InetAddress.getByName(MULTICAST_IP));
            this.table = table;
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    deleteGames();
                }
            }, 0, CLOSE_GAME_TIME);
        } catch (IOException ex) {
            close();
        }
    }

    private void deleteGames() {
        lastMsg.keySet().removeIf(n -> System.currentTimeMillis() - lastMsg.get(n) > DELETE_GAMES_TIME);
    }

    @Override
    public void startReceive() throws PacketReceiveException {
        byte[] buffer = new byte[BUFFER_MSG_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            while (!thread.isInterrupted()) {
                multicastSocket.receive(packet);
                GameMessage gameMessage = GameMessage.parseFrom(Arrays.copyOf(packet.getData(), packet.getLength()));
                if (gameMessage.hasAnnouncement()) {
                    AnnouncementMsg announceMsg = gameMessage.getAnnouncement();

                    String nameIpPort = null;
                    boolean hasMaster = false;
                    for (int i = 0; i < announceMsg.getPlayers().getPlayersCount(); i++) {
                        GamePlayer player = announceMsg.getPlayers().getPlayers(i);
                        if (player.getRole() == MASTER) {
                            nameIpPort = player.getName() + ":" + packet.getAddress() + ":" + packet.getPort();
                            hasMaster = true;
                            break;
                        }
                    }
                    String size = announceMsg.getConfig().getWidth() + ":" + announceMsg.getConfig().getHeight();
                    String food = announceMsg.getConfig().getFoodStatic() + ":" + announceMsg.getConfig().getFoodPerPlayer();
                    String playersNumber = Integer.toString(announceMsg.getPlayers().getPlayersCount());
                    Vector<String> nextCurrentGame = new Vector<>(Arrays.asList(nameIpPort, size, food, playersNumber));
                    if (!lastMsg.containsKey(nextCurrentGame) && hasMaster) {
                        table.addRow(nextCurrentGame);
                    }
                    lastMsg.put(nextCurrentGame, System.currentTimeMillis());
                }
            }
        } catch (IOException e) {
            throw new PacketReceiveException(AnnounceMsg.class + e.getMessage());
        }
    }

    @Override
    public void close() {
        multicastSocket.close();
        timer.cancel();
    }
}
