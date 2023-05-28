package controller;

import controller.node.Node;
import controller.parser.ConfigParser;
import exceptions.PacketSendException;
import org.jetbrains.annotations.NotNull;
import java.awt.event.KeyEvent;
import java.io.IOException;

import static proto.SnakesProto.Direction.*;


public record Controller(Node node) {

    public void moveSnake(@NotNull KeyEvent e) throws PacketSendException {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT -> node.sendSteerMsg(RIGHT);
            case KeyEvent.VK_LEFT -> node.sendSteerMsg(LEFT);
            case KeyEvent.VK_DOWN -> node.sendSteerMsg(DOWN);
            case KeyEvent.VK_UP -> node.sendSteerMsg(UP);
        }
    }

    public void createNewGame() throws IOException {
        node.setConfig(ConfigParser.createNewConfig(node.getProperties()));
        node.getStarter().createNewGame();
    }

    public void joinGame(boolean isViewer, String info) throws IOException {
        node.setConfig(ConfigParser.createNewConfig(node.getProperties()));
        node.getStarter().joinGame(isViewer, info);
    }
}
