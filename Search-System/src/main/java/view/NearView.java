package view;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Main;
import model.Near;

public class NearView extends JPanel {
    private boolean isActive = false;
    private final List<JPanel> listAttrs;
    private Near near;

    
    public NearView(Near near, SearchView searchView) {
    	this.near = near;
    	listAttrs = new ArrayList<>();
        setPlaceView(near);
        NearMouseListener mouseListener = new NearMouseListener(this, 
        		new PanelComponents("description", near.getXid(),
                Constants.ELEMENT, Constants.PANEL, searchView, true));
        listAttrs.forEach(e -> e.addMouseListener(mouseListener));
        addMouseListener(mouseListener);
    }
      

    public void setActive() {
        isActive = true;
        setBackground(Constants.ELEMENT);
        Arrays.stream(getComponents()).forEach(el -> el.setBackground(Constants.ELEMENT));
        Main.log.info(getClass() + ": " + near.getName() + " for xid " 
                    + near.getXid() + " has description: " + near.getDescription());
    }
    
    private void setPlaceView(Near near) {
        setBackground(Constants.ELEMENT);
        Map<String, Object> objMap = AnswerMaker.getAsMap((Object)near);

        for (var item : objMap.entrySet()) {     
            JLabel label = new JLabel(item.getValue().toString());

            setLayout(new BorderLayout());
            setBackground(Constants.ELEMENT);
            add(Box.createHorizontalStrut(10), BorderLayout.LINE_START);
            add(Box.createHorizontalStrut(10), BorderLayout.LINE_END);
            add(label);
        }
    }
    
    public List<JPanel> getListAttrs() {
        return listAttrs;
    }
    
    public boolean isActive() {
        return isActive;
    }   
}
