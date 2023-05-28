package model.actions;

import model.Model;
import proto.SnakesProto.*;
import proto.SnakesProto.GameState.*;

import java.util.List;
import java.util.Map;

import static model.info.State.*;

public class SnakesPlacer extends GameModel {

    public SnakesPlacer(Model model, Map<Integer, GamePlayer> players) {
        super(model, players);
    }

    private int calcY(Coord curCoord, int j) {
        return (curCoord.getY() + j + model.getFieldHeight()) % model.getFieldHeight();
    }

    private int calcX(Coord curCoord, int j) {
        return (curCoord.getX() + j + model.getFieldWidth()) % model.getFieldWidth();
    }

    public void putSnakesToField() {
        model.getSnakes().values().forEach(n -> {
            List<Coord> coords = n.getPointsList();
            Coord headCoord = coords.get(0);
            model.getField()[headCoord.getX()][headCoord.getY()].setState(SNAKE_HEAD);
            model.getField()[headCoord.getX()][headCoord.getY()].setId(n.getPlayerId());
            Coord curCoord = headCoord;
            for (int i = 1; i < coords.size(); i++) {
                int xOffset = coords.get(i).getX();
                int yOffset = coords.get(i).getY();
                if (xOffset == 0) {
                    if (yOffset < 0) {
                        for (int j = -1; j >= yOffset; j--) {
                            model.getField()[curCoord.getX()][calcY(curCoord, j)].setState(SNAKE_TAIL);
                            model.getField()[curCoord.getX()][calcY(curCoord, j)].setId(n.getPlayerId());
                        }
                    } else {
                        for (int j = 1; j <= yOffset; j++) {
                            model.getField()[curCoord.getX()][calcY(curCoord, j)].setState(SNAKE_TAIL);
                            model.getField()[curCoord.getX()][calcY(curCoord, j)].setId(n.getPlayerId());
                        }
                    }
                } else if (yOffset == 0) {
                    if (xOffset < 0) {
                        for (int j = -1; j >= xOffset; j--) {
                            model.getField()[calcX(curCoord, j)][curCoord.getY()].setState(SNAKE_TAIL);
                            model.getField()[calcX(curCoord, j)][curCoord.getY()].setId(n.getPlayerId());
                        }
                    } else {
                        for (int j = 1; j <= xOffset; j++) {
                            model.getField()[calcX(curCoord, j)][curCoord.getY()].setState(SNAKE_TAIL);
                            model.getField()[calcX(curCoord, j)][curCoord.getY()].setId(n.getPlayerId());
                        }
                    }
                }
                curCoord = model.createCoord((curCoord.getX() + xOffset + model.getFieldWidth())
                                % model.getFieldWidth(), (curCoord.getY() + yOffset + model.getFieldHeight())
                                % model.getFieldHeight());
            }
        });
    }
}
