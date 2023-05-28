import controller.Controller;
import controller.node.Node;
import view.View;

import java.io.IOException;

public class App {
    private final View view;

    public App() throws IOException {
        this.view = new View(new Controller(new Node()));
        view.initMenu();
    }
    public void runApp() {
        while (true) {
            try {
                view.getContentPane().getComponent(0).repaint();
            } catch (Exception ignored) {}
        }
    }
}
