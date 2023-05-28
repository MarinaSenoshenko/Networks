package model.actions;

import exceptions.PacketSendException;
import model.Model;
import proto.SnakesProto.*;
import proto.SnakesProto.GameState.*;

import java.net.UnknownHostException;
import java.util.*;

import static model.info.State.FOOD;
import static proto.SnakesProto.NodeRole.*;


public class CollisionsChecker extends GameModel {

    public CollisionsChecker(Model model, Map<Integer, GamePlayer> players) {
        super(model, players);
    }

    public void checkCollisions() {
        model.getFoods().removeIf(n -> {
            for (Snake snake : model.getSnakes().values()) {
                Coord head = snake.getPoints(0);
                if (head.getX() == n.getX() && head.getY() == n.getY()) {
                    return true;
                }
            }
            return false;
        });
        ArrayList<Snake> deadSnakes = new ArrayList<>();

        checkHeadCollisions(deadSnakes);
        checkAllCollisions(deadSnakes);
        removeDeadSnakes(deadSnakes);
    }

    private ArrayList<Coord> getNormalCoordinates(Snake snake) {
        ArrayList<Coord> normalCoords = new ArrayList<>();
        normalCoords.add(snake.getPoints(0));
        Coord curCoord = snake.getPoints(0);
        for (int i = 1; i < snake.getPointsCount(); i++) {
            int xOffset = snake.getPoints(i).getX();
            int yOffset = snake.getPoints(i).getY();
            if (xOffset == 0) {
                if (yOffset < 0) {
                    for (int j = -1; j >= yOffset; j--) {
                        normalCoords.add(model.createCoord(curCoord.getX(),
                                (curCoord.getY() + j + model.getConfig().getHeight()) %
                                        model.getConfig().getHeight()));
                    }
                } else {
                    for (int j = 1; j <= yOffset; j++) {
                        normalCoords.add(model.createCoord(curCoord.getX(),
                                (curCoord.getY() + j + model.getConfig().getHeight()) %
                                        model.getConfig().getHeight()));
                    }
                }
            } else if (yOffset == 0) {
                if (xOffset < 0) {
                    for (int j = -1; j >= xOffset; j--) {
                        normalCoords.add(model.createCoord((curCoord.getX() + j +
                                model.getConfig().getWidth()) % model.getConfig().getWidth(), curCoord.getY()));
                    }
                } else {
                    for (int j = 1; j <= xOffset; j++) {
                        normalCoords.add(model.createCoord((curCoord.getX() + j +
                                model.getConfig().getWidth()) % model.getConfig().getWidth(), curCoord.getY()));
                    }
                }
            }
            curCoord = model.createCoord((curCoord.getX() + xOffset + model.getConfig().getWidth()) %
                            model.getConfig().getWidth(), (curCoord.getY() + yOffset +
                    model.getConfig().getHeight()) % model.getConfig().getHeight());
        }
        return normalCoords;
    }

    private void removeDeadSnakes(ArrayList<Snake> deadSnakes) {
        deadSnakes.forEach(n -> {
            GamePlayer player = model.getPlayerById(n.getPlayerId());
            NodeRole prevRole = player.getRole();
            player = player.toBuilder().setRole(VIEWER).build();
            if (model.getNode().getRole() == MASTER && prevRole == DEPUTY) {
                try {
                    model.getNode().sendChangeRoleMessage(player.getId(), VIEWER);
                } catch (UnknownHostException | PacketSendException e) {
                    throw new RuntimeException(e);
                }
                model.getNode().findNewDeputy();
            }
            players.put(n.getPlayerId(), player);

            Random random = new Random();
            for (Coord coord : getNormalCoordinates(n)) {
                if (random.nextInt(2) == 1) {
                    model.getField()[coord.getX()][coord.getY()].setState(FOOD);
                    model.getFoods().add(coord);
                }
            }
            model.getSnakes().values().remove(n);
        });
    }

    private void checkHeadCollisions(ArrayList<Snake> deadSnakes) {
        for (Snake firstSnake : model.getSnakes().values()) {
            for (Snake secondSnake : model.getSnakes().values()) {
                if (firstSnake.getPoints(0).getX() == secondSnake.getPoints(0).getX() &&
                        firstSnake.getPoints(0).getY() == secondSnake.getPoints(0).getY()) {
                    if (firstSnake != secondSnake) {
                        deadSnakes.add(firstSnake);
                    }
                    break;
                }
            }
        }
    }

    private void checkAllCollisions(ArrayList<Snake> deadSnakes) {
        for (Snake firstSnake : model.getSnakes().values()) {
            Coord headCoord = firstSnake.getPoints(0);
            for (Snake secondSnake : model.getSnakes().values()) {
                if (firstSnake != secondSnake) {
                    for (Coord coord : getNormalCoordinates(secondSnake)) {
                        if (headCoord.getX() == coord.getX() && headCoord.getY() == coord.getY()) {
                            if (!deadSnakes.contains(firstSnake)) {
                                deadSnakes.add(firstSnake);
                            }
                            GamePlayer secondPlayer = model.getPlayerById(secondSnake.getPlayerId());
                            secondPlayer = secondPlayer.toBuilder()
                                    .setScore(secondPlayer.getScore() + 1)
                                    .build();
                            players.put(secondPlayer.getId(), secondPlayer);
                            break;
                        }
                    }
                } else {
                    for (Coord coord : getNormalCoordinates(secondSnake)) {
                        if (headCoord.getX() == coord.getX() &&
                                headCoord.getY() == coord.getY() && coord != headCoord) {
                            if (!deadSnakes.contains(firstSnake)) {
                                deadSnakes.add(firstSnake);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
