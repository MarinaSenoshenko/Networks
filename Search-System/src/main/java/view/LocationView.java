package view;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import main.Main;
import model.Location;

public class LocationView extends JPanel {
	private final List<JPanel> listAttrs;
	
    public LocationView(Location location, SearchView searchView, int index, LocationListView parent) {
    	listAttrs = new ArrayList<>();
    	setLocationView(location);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        LocationMouseListener mouseListener = new LocationMouseListener(this, 
                new PanelComponents("location", String.valueOf(index), Constants.ELEMENT, 
                Constants.PANEL, searchView, false), parent);
        
        listAttrs.forEach(e -> e.addMouseListener(mouseListener));
    }
    
    private void setLocationView(Location location) {
    	setBackground(Constants.ELEMENT);
        Map<String, Object> objMap = AnswerMaker.getAsMap(location);

        for (var item : objMap.entrySet()) {
            JLabel label = new JLabel(item.getKey() + ": " + item.getValue());
            JPanel panel = new JPanel();
            
            Main.log.info(getClass() + ": " + item.getKey() + ": " + item.getValue());
            
            panel.setLayout(new BorderLayout());
            panel.setBackground(Constants.ELEMENT);
            panel.add(Box.createHorizontalStrut(10), BorderLayout.LINE_START);
            panel.add(Box.createHorizontalStrut(10), BorderLayout.LINE_END);
            panel.add(label);
            listAttrs.add(panel);
            add(panel);
        }
    }
    
    public List<JPanel> getListAttrs() {
        return listAttrs;
    }
}
