package model.actions;

import model.info.Cell;
import model.Model;
import proto.SnakesProto.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static model.info.State.EMPTY;
import static model.info.State.FOOD;

public class FoodPlacer extends GameModel {

    public FoodPlacer(Model model, Map<Integer, GamePlayer> players) {
        super(model, players);
    }

    public void putFoodToField() {
        model.getFoods().forEach(n -> model.getField()[n.getX()][n.getY()].setState(FOOD));
    }

    private void randomPutFood(List<Cell> emptyCells, int foodAmount) {
        for (int i = 0; i < foodAmount; i++) {
            int cellFood = new Random().nextInt(emptyCells.size());
            model.getFoods().add(emptyCells.get(cellFood).getCoord());
            emptyCells.remove(cellFood);
        }
    }

    public void createEnoughFood() {
        int foodAmount = model.getConfig().getFoodStatic() + (int) (players.size() *
                model.getConfig().getFoodPerPlayer()) - model.getFoods().size();
        if (foodAmount > 0) {
            List<Cell> emptyCells = new ArrayList<>();
            for (int i = 0; i < model.getFieldWidth(); i++) {
                for (int j = 0; j < model.getFieldHeight(); j++) {
                    if (model.getField()[i][j].getState() == EMPTY) {
                        emptyCells.add(model.getField()[i][j]);
                    }
                }
            }
            randomPutFood(emptyCells, foodAmount);
        }
    }
}
