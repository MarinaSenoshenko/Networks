package model;

import lombok.Getter;
import model.info.Cell;
import model.info.State;
import proto.SnakesProto.*;
import proto.SnakesProto.GameState.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class GameStatistic {
    protected int stateOrder;
    protected ArrayList<Coord> foods = new ArrayList<>();
    protected Cell[][] field;
    protected GameConfig config;
    protected final Map<Integer, Snake> snakes = new ConcurrentHashMap<>();
    protected final Map<Integer, GamePlayer> players = new ConcurrentHashMap<>();
    protected final Map<Integer, GameMessage> directionChanges = new ConcurrentHashMap<>();


    public ArrayList<GamePlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public GamePlayer getPlayerById(int id) {
        return players.get(id);
    }

    public Coord createCoord(int x, int y) {
        return Coord.newBuilder().setX(x).setY(y).build();
    }

    public State getCellState(int x, int y) {
        return field[x][y].getState();
    }

    public GameState getState() {
        stateOrder++;
        return GameState.newBuilder()
                .addAllFoods(foods)
                .addAllSnakes(snakes.values())
                .setPlayers(GamePlayers.newBuilder().addAllPlayers(players.values()))
                .setConfig(config)
                .setStateOrder(stateOrder)
                .build();
    }

    public int getFieldWidth() {
        return config.getWidth();
    }

    public int getFieldHeight() {
        return config.getHeight();
    }
    public int getCellId(int x, int y) {
        return field[x][y].getId();
    }

    public int getPlayersCount() {
        return players.size();
    }

    public boolean isPlayerExists(int id) {
        return players.get(id) != null;
    }

    public void addChangeDirection(int id, GameMessage gameMessage) {
        if (directionChanges.get(id) == null ||
                directionChanges.get(id).getMsgSeq() < gameMessage.getMsgSeq()) {
            directionChanges.put(id, gameMessage);
        }
    }

    public synchronized void addPlayer(GamePlayer player) {
        players.put(player.getId(), player);
    }

    public synchronized void addSnake(Snake snake) {
        snakes.put(snake.getPlayerId(), snake);
    }

    public synchronized void removePlayer(int id) {
        players.values().removeIf(n -> n.getId() == id);
    }
}
