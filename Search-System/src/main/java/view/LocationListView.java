package view;

import javax.swing.*;

import model.Location;

import java.awt.*;
import java.util.List;

public class LocationListView extends JPanel {
    private LocationView chosen;

    public LocationListView(List<Location> locations, SearchView searchView) {
    	setBackground(Constants.BACKGROUND);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        add(Box.createVerticalStrut(10));

        for (int i = 0; i < locations.size(); i++) {
            LocationView mView = new LocationView(locations.get(i), searchView, i, this);
            mView.setPreferredSize(new Dimension(Constants.APP_LOCATION_LIST.getWidth(), Constants.APP_LOCATION_LIST.getHeight()));
            mView.setMaximumSize(new Dimension(Constants.APP_LOCATION_LIST.getWidth(), Constants.APP_LOCATION_LIST.getHeight()));
            add(Box.createVerticalStrut(10));
            add(mView);
        }
    }

    public synchronized LocationView getChosen() {
        return chosen;
    }


    public synchronized void setChosen(LocationView chosen) {
        this.chosen = chosen;
    }
}
