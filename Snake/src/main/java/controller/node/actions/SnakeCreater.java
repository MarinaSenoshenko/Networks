package controller.node.actions;

import controller.node.Node;
import proto.SnakesProto.GameState.Coord;
import proto.SnakesProto.Direction;
import proto.SnakesProto.GameState.Snake;

import java.util.Objects;
import java.util.Random;

import static model.info.State.EMPTY;
import static proto.SnakesProto.GameState.Snake.SnakeState.ALIVE;


public class SnakeCreater extends GameNode {
    private static final int UNKNOWN_SQUARE = -1;
    private static final int DIRECTION_COUNT = 4;
    private static final int OFFSET = 5;
    private static final int RESERVE = 2;

    public SnakeCreater(Node node) {
        super(node);
    }

    public Coord createCoord(int x, int y) {
        return Coord.newBuilder().setX(x).setY(y).build();
    }

    public Snake createSnake(int id, Coord headCoord) {
        Direction headDirection = Direction.forNumber(new Random().nextInt(DIRECTION_COUNT) + 1);
        Coord tailCoord;
        switch (Objects.requireNonNull(headDirection)) {
            case UP -> tailCoord = createCoord(0, 1);
            case DOWN -> tailCoord = createCoord(0, -1);
            case LEFT -> tailCoord = createCoord(1, 0);
            default -> tailCoord = createCoord(-1, 0);
        }
        return Snake.newBuilder()
                .setPlayerId(id)
                .addPoints(headCoord)
                .addPoints(tailCoord)
                .setHeadDirection(headDirection)
                .setState(ALIVE)
                .build();
    }

    private boolean isSquareGood(int x, int y) {
        for (int i = x; i < OFFSET; i++) {
            for (int j = y; j < OFFSET; j++) {
                if (node.getModel().getCellState(i, j) != EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public Coord findHeadCoord() {
        for (int i = 0; i < node.getModel().getFieldWidth() - OFFSET; i++) {
            for (int j = 0; j < node.getModel().getFieldHeight() - OFFSET; j++) {
                if (isSquareGood(i, j)) {
                    return createCoord(i + RESERVE, j + RESERVE);
                }
            }
        }
        return createCoord(UNKNOWN_SQUARE, UNKNOWN_SQUARE);
    }
}
