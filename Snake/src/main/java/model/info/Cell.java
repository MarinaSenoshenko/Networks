package model.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import proto.SnakesProto.GameState.*;

@Getter
@Setter
@AllArgsConstructor
public class Cell {
    private int id;
    private Coord coord;
    private State state;
}
