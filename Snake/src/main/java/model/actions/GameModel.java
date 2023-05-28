package model.actions;

import lombok.AllArgsConstructor;
import model.Model;
import proto.SnakesProto.*;

import java.util.Map;


@AllArgsConstructor
public class GameModel {
    protected final Model model;
    protected final Map<Integer, GamePlayer> players;
}
