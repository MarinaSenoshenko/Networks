package exceptions;

import java.io.IOException;

public class PacketReceiveException extends IOException {
    public PacketReceiveException(String string) {
        super(string);
    }
}
