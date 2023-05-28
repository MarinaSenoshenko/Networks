package socks5.server;

public enum States {
    WAIT_ClIENT_AUTH,
    SEND_CLIENT_AUTH,
    WAIT_CLIENT_REQ,
    SEND_CLIENT_RESP,
    FORWARDING,
    CONNECTING,
    SEND_ERR
}
