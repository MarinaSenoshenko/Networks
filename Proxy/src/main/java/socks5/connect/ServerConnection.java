package socks5.connect;

import socks5.server.States;
import lombok.Getter;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;
import static socks5.server.constants.Socks5Constants.*;
import static socks5.server.States.*;
import static socks5.server.constants.ConnectionConstants.*;

@Getter
public class ServerConnection implements Closeable, Connection {
    private boolean hasNoData = false;
    private States state;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
    private final SelectionKey serverSelectionKey;
    private final SelectionKey clientSelectionKey;
    private final SocketChannel socketChannel;

    public ServerConnection(SelectionKey serverSelectionKey, SelectionKey clientSelectionKey) {
        this.clientSelectionKey = clientSelectionKey;
        this.serverSelectionKey = serverSelectionKey;
        this.socketChannel = (SocketChannel)serverSelectionKey.channel();
        this.state = CONNECTING;
    }

    public void sendDataToServer() throws IOException {
        ClientConnection clientConnection = (ClientConnection)clientSelectionKey.attachment();
        socketChannel.write(clientConnection.getBuffer());
        if (!clientConnection.getBuffer().hasRemaining()) {
            serverSelectionKey.interestOps(serverSelectionKey.interestOps() & ~OP_WRITE);
            clientSelectionKey.interestOps(clientSelectionKey.interestOps() | OP_READ);
        }
    }

    public void getDataFromServer() throws IOException {
        buffer.clear();
        int readBytes = socketChannel.read(buffer);
        if (readBytes == INVALID_BUFF_SIZE) {
            hasNoData = true;
            serverSelectionKey.interestOps(serverSelectionKey.interestOps() & ~OP_READ);
            ClientConnection clientConnection = (ClientConnection)clientSelectionKey.attachment();
            clientConnection.shutDownOutput();
            if (clientConnection.isHasNoData()) {
                close();
            }
            return;
        }
        buffer.flip();
        serverSelectionKey.interestOps(serverSelectionKey.interestOps() & ~OP_READ);
        clientSelectionKey.interestOps(clientSelectionKey.interestOps() | OP_WRITE);
    }

    public void nextState() throws IOException {
        if (state == CONNECTING && serverSelectionKey.isConnectable()) {
            if (!socketChannel.finishConnect()) {
                throw new IOException();
            }
            state = FORWARDING;
            ((ClientConnection)clientSelectionKey.attachment()).makeResponse(SUCCESS);
            serverSelectionKey.interestOps(OP_READ);
            buffer.clear();
            buffer.flip();
        }
    }

    public boolean isHasNoData() {
        return hasNoData;
    }

    public void shutDownOutput() throws IOException {
        socketChannel.shutdownOutput();
    }

    @Override
    public void close() throws IOException {
        socketChannel.close();
        clientSelectionKey.channel().close();
    }
}
