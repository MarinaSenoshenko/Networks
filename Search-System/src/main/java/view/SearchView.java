package view;

import javax.swing.*;

import controller.ActionInView;
import main.Main;
import model.Location;
import model.Near;
import model.Weather;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchView {
    private ActionInView actionInView;
    private JPanel placesListPanel = new JPanel();
    private final JFrame frame = new JFrame("Maps app");
    private final JPanel mainPanel = new JPanel(new BorderLayout());
    private final JPanel weatherPanel = new JPanel();
    private final JPanel locationsListPanel = new JPanel();
    private final JPanel descriptionPanel = new JPanel();
    private final Map<String, NearView> nearViewList = new HashMap<>();

    public SearchView() {
    	ImageIcon icon = new ImageIcon(getClass().getResource("/icon.jpg"));
    	frame.setIconImage(icon.getImage());
    	
        frame.setSize(Constants.APP_FRAME.getWidth(), Constants.APP_FRAME.getHeight());
        frame.setLayout(new GridLayout(1,1));

        JPanel searchPanel = new JPanel();
        JTextField textField;
       
        searchPanel.setBackground(Constants.BACKGROUND);
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        textField = new JTextField();
        textField.setMaximumSize(new Dimension(Constants.MAX_SEARCH.getWidth(),  
        		Constants.MAX_SEARCH.getHeight()));
        textField.setBackground(Constants.SEARCH);
        searchPanel.add(textField);

        JButton button = new JButton("search");
        button.addActionListener(e -> searchLocations(textField));
        searchPanel.add(button);     
        
        mainPanel.setFocusable(true);
        mainPanel.requestFocusInWindow();
        
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        JPanel pageStartPanel = new JPanel(new BorderLayout());
        pageStartPanel.add(searchPanel, BorderLayout.CENTER);
        
        locationsListPanel.setPreferredSize(new Dimension(Constants.APP_LOCATION.getWidth(), 
        		Constants.APP_LOCATION.getHeight()));
        locationsListPanel.setLayout(new BorderLayout());
        locationsListPanel.setBackground(Constants.BACKGROUND);

        JPanel lineStartPanel = new JPanel(new BorderLayout());
        lineStartPanel.add(locationsListPanel, BorderLayout.CENTER);

        JPanel locationInfoPanel = new JPanel();
        locationInfoPanel.setBackground(Constants.BACKGROUND);
        locationInfoPanel.setLayout(new BorderLayout());
        
        
        mainPanel.add(locationInfoPanel, BorderLayout.CENTER);
        mainPanel.add(pageStartPanel, BorderLayout.PAGE_START);
        mainPanel.add(lineStartPanel, BorderLayout.LINE_START);
        
        placesListPanel.setBackground(Constants.BACKGROUND);
        placesListPanel.setLayout(new BorderLayout());
        
        weatherPanel.setBackground(Constants.BACKGROUND);
        
        placesListPanel.setLayout(new GridLayout(1, 1));
        locationInfoPanel.add(weatherPanel, BorderLayout.PAGE_START);
        locationInfoPanel.add(placesListPanel, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);

        frame.setLocation((int)((dimension.getWidth() - frame.getWidth()) / 2), 
        		          (int)((dimension.getHeight() - frame.getHeight()) / 2));
        
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setResizable(false);
    }
    
    private void searchLocations(JTextField textField) {
        if (!textField.getText().equals("")) {
            
            placesListPanel.removeAll();
            placesListPanel.repaint();
            
            descriptionPanel.removeAll();
        	descriptionPanel.repaint();

            this.setActionInView(new ActionInView("search", textField.getText()));
            Main.log.info(getClass() + ": search: " +  textField.getText());
        }
    }

    public void showLocations(List<Location> locations) {
    	Main.log.info(getClass() + ": " + "show locations...");
        Runnable task = () -> {
            locationsListPanel.removeAll();
            locationsListPanel.repaint();
            
            if (locations.isEmpty()) {
                JTextArea noResultsPanel = new JTextArea("No results :(", 25, 30);        
                noResultsPanel.setFont(new Font("Dialog", Font.PLAIN, 20));
                noResultsPanel.setTabSize(20);
                noResultsPanel.setLineWrap(true);
                noResultsPanel.setWrapStyleWord(true);
                noResultsPanel.setEditable(false);         
        	    
        		descriptionPanel.add(noResultsPanel, BorderLayout.BEFORE_FIRST_LINE);
        		
        		descriptionPanel.setVisible(true);
                JButton back = new JButton("search again");
                back.addActionListener(e -> {
                    frame.setContentPane(mainPanel);
                	frame.setVisible(true);
                });

                descriptionPanel.add(back, BorderLayout.AFTER_LAST_LINE);
                frame.setContentPane(descriptionPanel);
                frame.setVisible(true);
                Main.log.info(getClass() + ": " + "no location found");
                
            }
            else {
                LocationListView listView = new LocationListView(locations, this);
                JScrollPane scrollPane = new JScrollPane();
                scrollPane.setViewportView(listView);
                scrollPane.setBackground(Constants.BACKGROUND);
                scrollPane.setForeground(Constants.BACKGROUND);
                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());

                locationsListPanel.add(scrollPane, BorderLayout.CENTER);
                locationsListPanel.setVisible(true);
                frame.setVisible(true);
            }
        };

        SwingUtilities.invokeLater(task);
    }

    public void showWeather(Weather weather) {
    	Main.log.info(getClass() + ": show weather...");
        Runnable task = () -> {
        	weatherPanel.removeAll();
            weatherPanel.repaint();
            
            JPanel weatherView = new JPanel();
            
            
            weatherView.setBackground(Constants.SEARCH);
            String sgnStr;
            int temp = (int)weather.getTemp();            
            
            if (temp >= 0) {
                sgnStr = "+";
            } else {
                sgnStr = "-";
            }
            
            JLabel label = new JLabel("tempertature  " + sgnStr + temp);
            
            Main.log.info(getClass() + ": temperature " + sgnStr + temp);
          
            label.setForeground(Constants.PANEL);
            weatherView.add(label);            
            
            weatherPanel.add(weatherView);
        };

        SwingUtilities.invokeLater(task);
    }

    public void showNear(List<Near> nears) {
    	Main.log.info(getClass() + ": show places..." );
        Runnable task = () -> {
        	placesListPanel.removeAll();
            placesListPanel.repaint();

            JPanel listView = new JPanel();
            listView.setBackground(Constants.BACKGROUND);
            BoxLayout layout = new BoxLayout(listView, BoxLayout.Y_AXIS);
            listView.setLayout(layout);       
            
            weatherPanel.setMaximumSize(new Dimension(Constants.MAX_PLACE.getWidth(), 
            		Constants.MAX_PLACE.getHeight()));
            
            listView.add(weatherPanel);      
            listView.add(Box.createVerticalStrut(10));
            
            if (nears.isEmpty()) {
                Main.log.info(getClass() + ": " + "no near locations found...");
            }
            	
            for (Near near : nears) {     	
                NearView nearView = new NearView(near, this);
                
                if (near.getDescription() != null) {
                    nearView.setActive();
            	}
                nearView.setBackground(Constants.ELEMENT);
                Arrays.stream(nearView.getComponents()).toList().forEach(el -> 
                el.setBackground(Constants.ELEMENT));
                
                nearViewList.put(near.getXid(), nearView);
                
                nearView.setMaximumSize(new Dimension(Constants.MAX_PLACE.getWidth(), 
                		Constants.MAX_PLACE.getHeight()));

                listView.add(nearView);
                listView.add(Box.createVerticalStrut(10));
            }        
            frame.add(listView);
            

            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(listView);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setBackground(Constants.BACKGROUND);
            scrollPane.setForeground(Constants.BACKGROUND);

            placesListPanel.add(scrollPane);
            mainPanel.setVisible(true);
            frame.setVisible(true);
            this.placesListPanel = listView;
        };

        SwingUtilities.invokeLater(task);
    }
    
    public void showDescription(String description) {
        Runnable task = () -> { 
        	descriptionPanel.removeAll();
        	descriptionPanel.repaint();
            
        	descriptionPanel.setBackground(Constants.SEARCH);
        	descriptionPanel.setLayout(new BorderLayout());
            
            JTextArea placeDescription = new JTextArea(description, 25, 30);        
            placeDescription.setFont(new Font("Dialog", Font.PLAIN, 15));
            placeDescription.setTabSize(20);
            placeDescription.setLineWrap(true);
            placeDescription.setWrapStyleWord(true);
            placeDescription.setEditable(false);         
    	    
            placeDescription.setSize(Constants.APP_DESCRIPTION, Constants.APP_DESCRIPTION);
    		descriptionPanel.add(placeDescription, BorderLayout.CENTER);
    		
    		descriptionPanel.setVisible(true);
            JButton back = new JButton("back");
            back.addActionListener(e -> {
                frame.setContentPane(mainPanel);
            	frame.setVisible(true);
            });

            descriptionPanel.add(back, BorderLayout.BEFORE_FIRST_LINE);
            frame.setContentPane(descriptionPanel);
            frame.setVisible(true);
        };

        SwingUtilities.invokeLater(task);
    }

    
    public synchronized ActionInView waitActionInView() {
        if (actionInView == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ActionInView curAction = actionInView;
        actionInView = null;
        return curAction;
    }

    public synchronized void setActionInView(ActionInView actionInView) {
        this.actionInView = actionInView;
        notify();
    }
    
    public void setActive(String xid) {
        nearViewList.get(xid).setActive();
    }
}
