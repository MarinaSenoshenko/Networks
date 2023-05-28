package socks5.connect;

import socks5.server.Resolve;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.*;

import static java.nio.channels.SelectionKey.OP_WRITE;
import static org.xbill.DNS.DClass.IN;
import static org.xbill.DNS.Flags.RD;
import static org.xbill.DNS.Opcode.QUERY;
import static org.xbill.DNS.Rcode.NOERROR;
import static org.xbill.DNS.Section.ANSWER;
import static org.xbill.DNS.Section.QUESTION;
import static org.xbill.DNS.Type.A;
import static socks5.server.constants.ConnectionConstants.MAX_INDEX;
import static socks5.server.constants.Socks5Constants.BUF_SIZE;
import static socks5.server.constants.Socks5Constants.NOT_REACHABLE_HOST;

public class ResolveConnection implements Closeable, Connection {
    private int index = 0;
    private final InetSocketAddress dnsAddress = ResolverConfig.getCurrentConfig().servers().get(0);
    private final SelectionKey resolverSelectionKey;
    private final Deque<Resolve> resolves = new ArrayDeque<>();
    private final Map<Integer, Resolve> sentResolves = new HashMap<>();
    private final DatagramChannel datagramChannel;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);

    public ResolveConnection(SelectionKey resolverKey) {
        this.resolverSelectionKey = resolverKey;
        this.datagramChannel = (DatagramChannel) resolverKey.channel();
        this.resolverSelectionKey.interestOps(SelectionKey.OP_READ);
    }

    private int getNextIndex() {
        return (index > MAX_INDEX) ? 0 : index++;
    }

    public void addRequest(String address, ClientConnection clientConnection) {
        resolves.add(new Resolve(address, clientConnection));
        resolverSelectionKey.interestOps(resolverSelectionKey.interestOps() | OP_WRITE);
    }

    public void sendRequest() throws IOException {
        if (resolves.isEmpty()) {
            resolverSelectionKey.interestOps(resolverSelectionKey.interestOps() & ~OP_WRITE);
            return;
        }
        Resolve resolve = resolves.pop();
        int currentIndex = getNextIndex();
        sentResolves.put(currentIndex, resolve);

        Message message = new Message();
        Header header = message.getHeader();
        header.setOpcode(QUERY);
        header.setID(currentIndex);
        header.setFlag(RD);
        message.addRecord(Record.newRecord(new Name(resolve.address() + "."), A, IN), QUESTION);
        ByteBuffer newBuffer = ByteBuffer.wrap(message.toWire());
        datagramChannel.send(newBuffer, dnsAddress);
    }

    public void receiveRequest() throws IOException {
        buffer.clear();
        datagramChannel.receive(buffer);
        buffer.flip();

        Message message = new Message(buffer.array());
        if (message.getRcode() != NOERROR) {
            return;
        }
        int requestId = message.getHeader().getID();
        if (!sentResolves.containsKey(requestId)) {
            return;
        }
        List<Record> answers = message.getSection(ANSWER);
        ARecord aRecord = null;
        for (Record record : answers) {
            if (record.getType() == A) {
                aRecord = (ARecord)record;
                break;
            }
        }
        Resolve resolve = sentResolves.get(requestId);
        ClientConnection clientConnection = resolve.clientConnection();
        if (aRecord != null) {
            InetAddress address = aRecord.getAddress();
            clientConnection.connectToHost(address, clientConnection.getPortToConnect());
        } else {
            clientConnection.makeResponse(NOT_REACHABLE_HOST);
        }
        sentResolves.remove(requestId);
    }

    @Override
    public void close() throws IOException {
        datagramChannel.close();
    }
}
