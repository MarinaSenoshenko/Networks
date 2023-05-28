package controller.node;

import controller.node.actions.GameStarter;
import controller.node.actions.SnakeCreater;
import controller.node.info.MasterInfo;
import lombok.Getter;
import lombok.Setter;
import proto.SnakesProto.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

import static proto.SnakesProto.NodeRole.DEPUTY;
import static proto.SnakesProto.NodeRole.NORMAL;

@Setter
@Getter
public class Node extends MsgHandler {
    private GameConfig config;
    private GameState state;
    private final Properties properties = new Properties();
    private final GameStarter starter = new GameStarter(this);
    private final SnakeCreater snakeCreater = new SnakeCreater(this);

    public Node() throws SocketException {
        super();
        try {
            properties.load(Node.class.getResourceAsStream("/snake.properties"));
        } catch (IOException ex) {
            close();
        }
    }

   public void updateModelFromState(GameState state) {
        if (this.state == null) {
            config = state.getConfig();
        }
        this.state = state;
        model.modelFromState(state);
    }

    private boolean hasDeputy() {
        ArrayList<GamePlayer> players = model.getPlayers();
        for (GamePlayer player : players) {
            if (player.getRole() == DEPUTY) {
                return true;
            }
        }
        return false;
    }

    public void findNewDeputy() {
        if (!hasDeputy()) {
            for (GamePlayer player : model.getPlayers()) {
                if (player.getRole() == NORMAL) {
                    try {
                        gameMsgSender.sendRoleChangeMsg(id, player.getId(), role,
                                DEPUTY, InetAddress.getByName(player.getIpAddress()), player.getPort());
                        break;
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }


    public void makeDeputyMaster() {
        GamePlayers players = state.getPlayers();
        for (int i = 0; i < players.getPlayersCount(); i++) {
            GamePlayer player = players.getPlayers(i);
            if (player.getRole() == DEPUTY) {
                try {
                    masterInfo = new MasterInfo(player.getId(), player.getPort(),
                            InetAddress.getByName(player.getIpAddress()));
                    break;
                } catch (IOException ignored) {
                }
            }
        }
    }

    public GamePlayer createGamePlayer(int id, NodeRole role, String ip, int port, String name) {
        return GamePlayer.newBuilder().setId(id).setRole(role).setIpAddress(ip)
                .setPort(port).setName(name).setScore(0).build();
    }

    public Vector<Vector<String>> getPlayersScore() {
        Vector<Vector<String>> score = new Vector<>();
        for (GamePlayer player : model.getPlayers()) {
            score.add(new Vector<>(Arrays.asList(player.getName(),
                    Integer.toString(player.getScore()))));
        }
        return score;
    }
}
