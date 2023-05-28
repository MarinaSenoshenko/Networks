package model.actions;

import model.Model;
import proto.SnakesProto.GameState.*;
import proto.SnakesProto.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static model.info.State.FOOD;
import static proto.SnakesProto.Direction.*;
import static proto.SnakesProto.GameState.Snake.SnakeState.ALIVE;

public class SnakeDirections extends GameModel {

    public SnakeDirections(Model model, Map<Integer, GamePlayer> players) {
        super(model, players);
    }


    private void stepSnake(List<Coord> points) {
        Coord point = points.get(points.size() - 1);
        points.remove(points.size() - 1);

        if (point.getX() < 0) {
            if (point.getX() != -1) {
                points.add(model.createCoord(point.getX() + 1, 0));
            }
        } else if (point.getX() > 0) {
            if (point.getX() != 1) {
                points.add(model.createCoord(point.getX() - 1, 0));
            }
        } else {
            if (point.getY() < 0) {
                if (point.getY() != -1) {
                    points.add(model.createCoord(0, point.getY() + 1));
                }
            } else if (point.getY() > 0) {
                if (point.getY() != 1) {
                    points.add(model.createCoord(0, point.getY() - 1));
                }
            }
        }
    }

    private void growSnake(Snake snake) {
        GamePlayer player = players.get(snake.getPlayerId());
        player = player.toBuilder()
                .setScore(player.getScore() + 1)
                .build();
        players.put(player.getId(), player);
    }

    public synchronized void moveSnakes() {
        model.getSnakes().values().forEach(n -> {
            List<Coord> newPoints = new ArrayList<>();
            Coord prevHead = n.getPoints(0);
            Coord newPoint;
            switch (n.getHeadDirection()) {
                case UP -> newPoint = model.createCoord(prevHead.getX(),
                        (prevHead.getY() - 1 + model.getFieldHeight()) % model.getFieldHeight());
                case DOWN -> newPoint = model.createCoord(prevHead.getX(),
                        (prevHead.getY() + 1 + model.getFieldHeight()) % model.getFieldHeight());
                case RIGHT -> newPoint = model.createCoord((prevHead.getX() + 1 +
                        model.getFieldWidth()) % model.getFieldWidth(), prevHead.getY());
                default -> newPoint = model.createCoord((prevHead.getX() - 1 +
                        model.getFieldWidth()) % model.getFieldWidth(), prevHead.getY());
            }
            newPoints.add(newPoint);

            switch (n.getHeadDirection()) {
                case UP -> newPoint = model.createCoord(0, 1);
                case DOWN -> newPoint = model.createCoord(0, -1);
                case RIGHT -> newPoint = model.createCoord(-1, 0);
                default -> newPoint = model.createCoord(1, 0);
            }
            newPoints.add(newPoint);

            for (int i = 1; i < n.getPointsCount(); i++) {
                newPoints.add(n.getPoints(i));
            }

            Coord head = newPoints.get(0);
            if (model.getField()[head.getX()][head.getY()].getState() != FOOD) {
                stepSnake(newPoints);
            } else {
                growSnake(n);
            }

            Snake newSnake = Snake.newBuilder()
                    .setHeadDirection(n.getHeadDirection())
                    .setPlayerId(n.getPlayerId())
                    .addAllPoints(newPoints)
                    .setState(ALIVE)
                    .build();
            model.getSnakes().put(n.getPlayerId(), newSnake);
        });
    }

    private boolean isMoveCorrect(Direction direction1, Direction direction2) {
        if (!(direction1 == UP && direction2 == DOWN) &&
                !(direction1 == DOWN && direction2 == UP) &&
                !(direction1 == RIGHT && direction2 == LEFT) &&
                !(direction1 == LEFT && direction2 == RIGHT)) {
            return true;
        }
        return false;
    }


    public void changeSnakesDirection() {
        model.getDirectionChanges().keySet().forEach(n -> {
            Snake snake = model.getSnakes().get(n);
            if (snake != null) {
                Direction prevDirection = snake.getHeadDirection();
                Direction newDirection = model.getDirectionChanges().get(n).getSteer().getDirection();
                if (isMoveCorrect(prevDirection, newDirection)) {
                    snake = snake.toBuilder().setHeadDirection(newDirection).build();
                    model.getSnakes().put(n, snake);
                }
            }
        });
        model.getDirectionChanges().clear();
    }
}
