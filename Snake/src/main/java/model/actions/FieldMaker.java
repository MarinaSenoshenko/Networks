package model.actions;

import model.Model;
import proto.SnakesProto.*;

import java.util.Map;

import static model.info.State.EMPTY;


public class FieldMaker extends GameModel {

    public FieldMaker(Model model, Map<Integer, GamePlayer> players) {
        super(model, players);
    }

    public void emptyField() {
        for (int i = 0; i < model.getFieldWidth(); i++) {
            for (int j = 0; j < model.getFieldHeight(); j++) {
                model.getField()[i][j].setState(EMPTY);
                model.getField()[i][j].setId(0);
            }
        }
    }
}
