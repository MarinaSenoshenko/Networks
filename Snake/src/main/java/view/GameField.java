package view;

import controller.Controller;
import exceptions.PacketSendException;
import model.Model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import static view.ViewConstants.*;

public class GameField extends JPanel {
    private final JTable table;
    private final KeyAdapter adapter;
    private final Controller controller;
    private final Model model;

    public GameField(View view) {
        setLayout(null);
        this.controller = view.getController();
        this.model = controller.node().getModel();

        Vector<String> columnNames = new Vector<>(Arrays.asList("Name", "Score"));

        table = new JTable(new Vector<Vector<String>>(), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(COMPONENTS_X, SCORE_TABLE_Y, SCORE_TABLE_WIDTH, SCORE_TABLE_HEIGHT);
        add(pane);

        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(SCORE_TABLE_X, SCORE_TABLE_Y + SCORE_TABLE_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);

        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.node().stopPlaying();
                try {
                    view.initMenu();
                } catch (IOException ignored) {
                    throw new RuntimeException();
                }
            }
        });
        add(exitButton);

        adapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                try {
                    controller.moveSnake(e);
                } catch (PacketSendException ignored) {
                     throw new RuntimeException();
                }
            }
        };
        addKeyListener(adapter);
    }

    public JPanel createGamePlayer() {
        addKeyListener(adapter);
        return this;
    }

    public JPanel createGameViewer() {
        if (listenerList.getListenerCount() > 0) {
            removeKeyListener(adapter);
        }
        return this;
    }

    private Color fieldCellColor(int i, int j) {
        if ((i + j) % 2 == 0) {
            return DARK_GREEN;
        }
        return LIGHT_GREEN;
    }

    private Color playerSnakeColor(int i, int j) {
        if (controller.node().getId() == model.getCellId(i, j)) {
            return Color.YELLOW;
        }
        return Color.RED;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
        tableModel.setRowCount(0);
        Vector<Vector<String>> score = controller.node().getPlayersScore();
        for (Vector<String> strings : score) {
            tableModel.addRow(strings);
        }

        int field = Math.min(GAME_FIELD_SIZE / model.getFieldWidth(),
                                GAME_FIELD_SIZE / model.getFieldHeight());
                                g.setColor(Color.GREEN);

        g.fillRect(0, 0, field * model.getFieldWidth(),
                field * model.getFieldHeight());

        for (int i = 0; i < model.getFieldWidth(); i++) {
            for (int j = 0; j < model.getFieldHeight(); j++) {
                switch (model.getCellState(i, j)) {
                    case SNAKE_TAIL, SNAKE_HEAD -> {
                        g.setColor(playerSnakeColor(i, j));
                        g.fillRect(i * field+ 1, j * field + 1, field - 1, field - 1);
                    }
                    case EMPTY -> {
                        g.setColor(fieldCellColor(i, j));
                        g.fillRect(i * field + 1, j * field + 1, field - 1, field - 1);
                    }
                    case FOOD -> {
                        g.setColor(fieldCellColor(i, j));
                        g.fillRect(i * field + 1, j * field + 1, field - 1, field - 1);
                        g.setColor(Color.MAGENTA);
                        g.fillOval(i * field + 1, j * field + 1, field - 1, field - 1);
                    }
                }
            }
        }
    }
}
