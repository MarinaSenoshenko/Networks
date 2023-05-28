package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import controller.ActionInView;

public class LocationMouseListener extends MouseAdapter {
    private final LocationListView locationListView;
    private final LocationView locationView;
	private final PanelComponents panelComponents;

    public LocationMouseListener(LocationView locationView, PanelComponents panelComponents, 
    		LocationListView locationListView) {
    	this.locationView = locationView;
    	this.panelComponents = panelComponents;
        this.locationListView = locationListView;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!locationView.equals(locationListView.getChosen())) {
        	locationView.setBackground(panelComponents.enteredColor());
        	locationView.getListAttrs().forEach(el -> 
        	el.setBackground(panelComponents.enteredColor()));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!locationView.equals(locationListView.getChosen())) {
        	locationView.setBackground(panelComponents.color());
        	locationView.getListAttrs().forEach(el -> 
        	el.setBackground(panelComponents.color()));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!locationView.equals(locationListView.getChosen())) {
        	if (panelComponents.changeOnClick()) {
        		locationView.setBackground(panelComponents.color());
        		locationView.getListAttrs().forEach(el -> 
        		el.setBackground(panelComponents.color()));
            }
        	
        	panelComponents.frame().setActionInView(new ActionInView(panelComponents.actionName(), 
        			panelComponents.actionParam()));
        	
            
        	locationView.setBackground(panelComponents.enteredColor());
        	locationView.getListAttrs().forEach(el -> 
        	el.setBackground(panelComponents.enteredColor()));
            if (locationListView.getChosen() != null) {
            	locationView.setBackground(panelComponents.color());
            	locationView.getListAttrs().forEach(el -> 
            	el.setBackground(panelComponents.color()));
            }
            locationListView.setChosen(locationView);
        }
    }
}
