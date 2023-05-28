package view;

import controller.Controller;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import static view.ViewConstants.BUTTON_HEIGHT;
import static view.ViewConstants.BUTTON_WIDTH;

public class Menu extends JPanel {

    public Menu(Controller controller, View view) throws IOException {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JButton newGameButton = new JButton("New game");
        newGameButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        newGameButton.setAlignmentX(CENTER_ALIGNMENT);
        newGameButton.setFocusPainted(false);
        newGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                controller.node().stopReceiveAnnouncement();
                try {
                    controller.createNewGame();
                    view.initGameField();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        add(newGameButton);
        JCheckBox isViewerBox = new JCheckBox("Viewer");
        isViewerBox.setAlignmentX(CENTER_ALIGNMENT);
        add(isViewerBox);
        Vector<String> columnNames = new Vector<>(Arrays.asList("Server", "Size", "Food", "Players"));

        JTable table = new JTable(new Vector<Vector<String>>(), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
                    String info = (String)tableModel.getValueAt(table.getSelectedRow(), 0);
                    boolean isViewer = isViewerBox.isSelected();
                    controller.node().stopReceiveAnnouncement();
                    try {
                        controller.joinGame(isViewer, info);
                        view.initGameField();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        JLabel label = new JLabel("For join double tap to one of existing games");
        label.setAlignmentX(CENTER_ALIGNMENT);
        add(label);

        add(new JScrollPane(table));
        controller.node().startReceiveAnnouncement((DefaultTableModel)table.getModel());
    }
}
