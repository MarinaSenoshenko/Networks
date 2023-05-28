import socks5.Server;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Please type port number");
            System.exit(1);
        }
        try {
            int port = Integer.parseInt(args[0]);
            new Server(port).run();
        } catch (NumberFormatException ex) {
            System.err.println("Please type port as digit");
            ex.printStackTrace();
        }
    }
}
