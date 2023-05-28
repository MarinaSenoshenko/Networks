package socks5.connect;

import socks5.server.States;
import lombok.Getter;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static java.nio.channels.SelectionKey.*;
import static socks5.server.States.*;
import static socks5.server.constants.Socks5Constants.*;
import static socks5.server.constants.ConnectionConstants.*;


@Getter
public class ClientConnection implements Closeable, Connection {
    private int portToConnect;
    private boolean hasNoData = false;
    private States state;
    private SelectionKey serverSelectionKey;
    private final SelectionKey clientSelectionKey;
    private final SocketChannel socketChannel;
    private final ResolveConnection resolveConnection;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);

    public ClientConnection(SelectionKey clientSelectionKey, ResolveConnection resolveConnection) {
        this.resolveConnection = resolveConnection;
        this.clientSelectionKey = clientSelectionKey;
        this.state = WAIT_ClIENT_AUTH;
        this.socketChannel = (SocketChannel) clientSelectionKey.channel();
    }

    public void nextState() throws IOException {
        if (state == WAIT_ClIENT_AUTH) {
            makeAuthResponse();
        } else if (state == WAIT_CLIENT_REQ) {
            processRequest();
        } else if (state == SEND_CLIENT_AUTH && !buffer.hasRemaining()) {
            state = WAIT_CLIENT_REQ;
            buffer.clear();
        } else if (state == SEND_CLIENT_RESP && !buffer.hasRemaining()) {
            state = FORWARDING;
            clientSelectionKey.interestOps(OP_READ);
        } else if (state == SEND_ERR && !buffer.hasRemaining()) {
            close();
            buffer.clear();
        }
    }

    public void sendDataToClient() throws IOException {
        ServerConnection serverConnection = (ServerConnection) serverSelectionKey.attachment();
        socketChannel.write(serverConnection.getBuffer());
        if (!serverConnection.getBuffer().hasRemaining()) {
            clientSelectionKey.interestOps(clientSelectionKey.interestOps() & ~OP_WRITE);
            serverSelectionKey.interestOps(serverSelectionKey.interestOps() | OP_READ);
        }
    }

    public void getDataFromClient() throws IOException {
        buffer.clear();
        if (socketChannel.read(buffer) == INVALID_BUFF_SIZE) {
            hasNoData = true;
            clientSelectionKey.interestOps(clientSelectionKey.interestOps() & ~OP_READ);
            ServerConnection serverConnection = (ServerConnection) serverSelectionKey.attachment();
            serverConnection.shutDownOutput();
            if (serverConnection.isHasNoData()) {
                close();
            }
            return;
        }
        buffer.flip();
        clientSelectionKey.interestOps(clientSelectionKey.interestOps() & ~OP_READ);
        serverSelectionKey.interestOps(serverSelectionKey.interestOps() | OP_WRITE);
    }

    public void connectToHost(InetAddress address, int port) throws IOException {
        SocketChannel serverChannel = SocketChannel.open();
        serverChannel.configureBlocking(false);
        serverSelectionKey = serverChannel.register(clientSelectionKey.selector(), OP_CONNECT);
        serverSelectionKey.attach(new ServerConnection(serverSelectionKey, clientSelectionKey));
        serverChannel.connect(new InetSocketAddress(address, port));
    }

    public void makeResponse(byte error) {
        buffer.clear();
        buffer.put(VERSION);
        buffer.put(error);
        buffer.put(RESERVED_BYTE);
        buffer.put(IPV4);
        for (int i = 0; i < RESERVED; i++) {
            buffer.put(RESERVED_BYTE);
        }
        buffer.flip();
        state = (error == SUCCESS) ? SEND_CLIENT_RESP : SEND_ERR;
        clientSelectionKey.interestOps(OP_WRITE);
    }

    public void processRequest() throws IOException {
        int bufferSize = buffer.position();
        if (bufferSize < MIN_BUFF_SIZE) {
            return;
        }
        if (buffer.get(CONNECTION_TYPE_POSITION) != TCP_CONNECT) {
            makeResponse(NOT_SUPPORTED_CMD);
            return;
        }
        byte addressType = buffer.get(ADDRESS_TYPE_POSITION);
        if (addressType == IPV4) {
            if (bufferSize < MIN_IPV4_BUFF_SIZE) {
                return;
            }
            byte[] address = new byte[IPV4_ADDRESS_BYTE_SIZE];
            buffer.position(IPV4_ADDRESS_POSITION);
            buffer.get(address);
            int port = buffer.getShort(PORT_POSITION);
            InetAddress inetAddress = InetAddress.getByAddress(address);
            connectToHost(inetAddress, port);
            clientSelectionKey.interestOps(0);
            state = FORWARDING;
        } else if (addressType == DOMAIN_NAME) {
            int addressLength = buffer.get(ADDRESS_LENGTH_POSITION);
            if (bufferSize < RESERVED + addressLength) {
                return;
            }
            byte[] address = new byte[addressLength];
            buffer.position(DOMAIN_NAME_POSITION);
            buffer.get(address, 0, addressLength);
            String addressStr = new String(address);
            clientSelectionKey.interestOps(0);
            state = FORWARDING;
            resolveConnection.addRequest(addressStr, this);
            portToConnect = buffer.getShort(5 + addressLength);
        } else {
            makeResponse(NOT_SUPPORTED_TYPE);
        }
    }

    public void makeAuthResponse() {
        int methodsNumber = buffer.get(METHODS_NUMBER_POSITION);
        byte method = AUTH_NOT_FOUND;
        for (int i = 0; i < methodsNumber; i++) {
            byte currentMethod = buffer.get(i + OFFSET);
            System.out.println(currentMethod);
            if (currentMethod == NO_AUTH) {
                method = currentMethod;
                break;
            }
        }
        buffer.clear();
        buffer.put(VERSION);
        buffer.put(method);
        buffer.flip();
        clientSelectionKey.interestOps(OP_WRITE);

        state = (method == AUTH_NOT_FOUND) ? SEND_ERR : SEND_CLIENT_AUTH;
    }

    public void read() throws IOException {
        socketChannel.read(buffer);
    }

    public void write() throws IOException {
        socketChannel.write(buffer);
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
        if (serverSelectionKey != null) {
            serverSelectionKey.channel().close();
        }
    }
}
