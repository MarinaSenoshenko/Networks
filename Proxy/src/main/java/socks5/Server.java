package socks5;

import socks5.connect.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

import static java.nio.channels.SelectionKey.OP_READ;
import static socks5.server.constants.ConnectionConstants.INVALID_SELECT;

public class Server implements Runnable, Closeable {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private ResolveConnection resolveConnection;
    private DatagramChannel resolverSocket;
    private final ConnectionProcessor connectionProcessor = new ConnectionProcessor();

    public static class BasicConnection {
    }

    public Server(int port) {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port));
            System.out.println("Server started in port " + port);
            selector = SelectorProvider.provider().openSelector();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, serverSocketChannel.validOps(), new BasicConnection());

            resolverSocket = DatagramChannel.open();
            resolverSocket.configureBlocking(false);
            SelectionKey resolverKey = resolverSocket.register(selector, 0);
            resolveConnection = new ResolveConnection(resolverKey);
            resolverKey.attach(resolveConnection);
        } catch (IOException ex) {
            System.err.println("Can not create server");
            ex.printStackTrace();
            close();
        }
    }

    @Override
    public void run() {
        try {
            while (selector.select() > INVALID_SELECT) {
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    Connection connection = (Connection)key.attachment();

                    if (connection instanceof BasicConnection) {
                        if (key.isAcceptable()) {
                            accept(key);
                        }
                    } else if (connection instanceof ClientConnection) {
                        try {
                            connectionProcessor.processClientConnection(key);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            ((ClientConnection)connection).close();
                        }
                    } else if (connection instanceof ServerConnection) {
                        try {
                            connectionProcessor.processServerConnection(key);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            ((ServerConnection)connection).close();
                        }
                    } else if (connection instanceof ResolveConnection) {
                        try {
                            connectionProcessor.processResolverConnection(key);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            ((ResolveConnection)key.attachment()).close();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            close();
        }
    }

    private void accept(SelectionKey key) throws IOException {
        SocketChannel channel = ((ServerSocketChannel)key.channel()).accept();
        channel.configureBlocking(false);
        SelectionKey newKey = channel.register(selector, OP_READ);
        newKey.attach(new ClientConnection(newKey, resolveConnection));
    }

    @Override
    public void close() {
        try {
            if (serverSocketChannel != null) {
                serverSocketChannel.close();
            }
            if (resolverSocket != null) {
                resolverSocket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
