package model;

import controller.node.Node;
import lombok.Getter;
import model.actions.*;
import model.info.Cell;
import proto.SnakesProto.*;
import proto.SnakesProto.GameState.*;

import java.io.IOException;
import java.util.*;

import static model.info.State.EMPTY;

@Getter
public class Model extends GameStatistic {
    private final Node node;
    private final CollisionsChecker collisionsChecker = new CollisionsChecker(this, players);
    private final SnakeDirections snakeDirections = new SnakeDirections(this, players);
    private final FieldMaker fieldMaker = new FieldMaker(this, players);
    private final FoodPlacer foodPlacer = new FoodPlacer(this, players);
    private final SnakesPlacer snakesPlacer = new SnakesPlacer(this, players);


    public Model(GameConfig config, Node node) throws IOException {
        super.config = config;
        this.node = node;
        initField();
    }

    public void createGameField() {
        fieldMaker.emptyField();
        foodPlacer.putFoodToField();
        snakeDirections.changeSnakesDirection();
        snakeDirections.moveSnakes();
        collisionsChecker.checkCollisions();
        snakesPlacer.putSnakesToField();
        foodPlacer.createEnoughFood();
    }

    private void initField() {
        int height = config.getHeight();
        int width = config.getWidth();
        field = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                field[i][j] = new Cell(0,
                        Coord.newBuilder().setX(i).setY(j).build(), EMPTY);
            }
        }
    }

    public void modelFromState(GameState state) {
        fieldMaker.emptyField();
        stateOrder = state.getStateOrder();
        players.clear();
        for (int i = 0; i < state.getPlayers().getPlayersCount(); i++) {
            GamePlayer player = state.getPlayers().getPlayers(i);
            players.put(player.getId(), player);
        }
        snakes.clear();
        for (int i = 0; i < state.getSnakesCount(); i++) {
            Snake snake = state.getSnakesList().get(i);
            snakes.put(snake.getPlayerId(), snake);
        }
        foods = new ArrayList<>(state.getFoodsList());
        foodPlacer.putFoodToField();
        snakesPlacer.putSnakesToField();
    }
}
