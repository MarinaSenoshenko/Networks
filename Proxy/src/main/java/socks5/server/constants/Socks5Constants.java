package socks5.server.constants;

public class Socks5Constants {
    public static byte SUCCESS = 0x00;
    public static final byte NO_AUTH = 0x00;
    public static final byte RESERVED_BYTE = 0x00;
    public static final byte IPV4 = 0x01;
    public static final byte TCP_CONNECT = 0x01;
    public static final byte DOMAIN_NAME = 0x03;
    public static byte NOT_REACHABLE_HOST = 0x04;
    public static final byte VERSION = 0x05;
    public static byte NOT_SUPPORTED_CMD = 0x07;
    public static byte NOT_SUPPORTED_TYPE = 0x08;
    public static final byte AUTH_NOT_FOUND = (byte) 0xFF;
    public static final int BUF_SIZE = 1024;
}
