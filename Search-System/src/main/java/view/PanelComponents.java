package view;

import java.awt.Color;

public record PanelComponents(String actionName, String actionParam, Color color, Color enteredColor, SearchView frame,
        boolean changeOnClick) {

}
