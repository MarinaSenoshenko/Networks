package socks5.server;

import socks5.connect.ClientConnection;

public record Resolve(String address, ClientConnection clientConnection) {

}
