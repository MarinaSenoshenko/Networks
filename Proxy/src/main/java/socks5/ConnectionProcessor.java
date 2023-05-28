package socks5;

import socks5.connect.ClientConnection;
import socks5.connect.ResolveConnection;
import socks5.connect.ServerConnection;
import socks5.server.States;
import lombok.NoArgsConstructor;
import java.io.IOException;
import java.nio.channels.SelectionKey;

import static socks5.server.States.*;

@NoArgsConstructor
public class ConnectionProcessor {
    void processClientConnection(SelectionKey clientKey) throws IOException {
        ClientConnection clientConnection = (ClientConnection) clientKey.attachment();
        States state = clientConnection.getState();
        if (state == WAIT_ClIENT_AUTH || state == WAIT_CLIENT_REQ) {
            clientConnection.read();
        } else if (state == SEND_CLIENT_AUTH || state == SEND_CLIENT_RESP || state == SEND_ERR) {
            clientConnection.write();
        } else if (state == FORWARDING && clientKey.isReadable()) {
            clientConnection.getDataFromClient();
        } else if (state == FORWARDING && clientKey.isWritable()) {
            clientConnection.sendDataToClient();
        }
        clientConnection.nextState();
    }

    void processServerConnection(SelectionKey serverKey) throws IOException {
        ServerConnection serverConnection = (ServerConnection)serverKey.attachment();
        if (serverConnection.getState() == FORWARDING) {
            if (serverKey.isReadable()) {
                serverConnection.getDataFromServer();
            } else if (serverKey.isWritable()) {
                serverConnection.sendDataToServer();
            }
        }
        serverConnection.nextState();
    }

    void processResolverConnection(SelectionKey resolverKey) throws IOException {
        ResolveConnection resolveConnection = (ResolveConnection)resolverKey.attachment();
        if (resolverKey.isReadable()) {
            resolveConnection.receiveRequest();
        } else if (resolverKey.isWritable()) {
            resolveConnection.sendRequest();
        }
    }
}
