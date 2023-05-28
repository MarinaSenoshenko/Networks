package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import controller.ActionInView;

public class NearMouseListener extends MouseAdapter {
	private final NearView nearView;
	private final PanelComponents panelComponents;

	
    public NearMouseListener(NearView nearView, PanelComponents panelComponents) {
    	this.nearView = nearView;
    	this.panelComponents = panelComponents;    	
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (nearView.isActive()) {
        	nearView.setBackground(panelComponents.enteredColor());
        	nearView.getListAttrs().forEach(el -> 
        	el.setBackground(panelComponents.enteredColor()));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (nearView.isActive()) {
        	nearView.setBackground(panelComponents.color());
        	nearView.getListAttrs().forEach(el -> 
        	el.setBackground(panelComponents.color()));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (nearView.isActive()) {
        	if (panelComponents.changeOnClick()) {
                mouseExited(e);
            }

        	panelComponents.frame().setActionInView(new ActionInView(panelComponents.actionName(), 
        			panelComponents.actionParam()));
        }
    }
}
