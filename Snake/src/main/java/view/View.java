package view;

import controller.Controller;
import lombok.Getter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;

import static proto.SnakesProto.NodeRole.VIEWER;
import static view.ViewConstants.*;

@Getter
public class View extends JFrame {
    private final Controller controller;

    public View(Controller controller) {
        super("Snake");
        this.controller = controller;
        ImageIcon icon = new ImageIcon(Objects
                .requireNonNull(View.class.getResource("/snake.jpg")));
        setIconImage(icon.getImage());
    }

    public void initMenu() throws IOException {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                controller.node().close();
            }
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        JPanel menuPanel = new Menu(controller, this);
        getContentPane().removeAll();
        getContentPane().add(menuPanel);
        revalidate();
        menuPanel.requestFocus();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public void initGameField() {
        JPanel gamePanel = (controller.node().getRole() == VIEWER) ?
            new GameField(this).createGameViewer() :
            new GameField(this).createGamePlayer();

        getContentPane().removeAll();
        getContentPane().add(gamePanel);
        revalidate();
        gamePanel.requestFocus();
        setLocationRelativeTo(null);
    }
}
