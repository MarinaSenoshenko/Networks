package exceptions;

import java.io.IOException;

public class PacketSendException extends IOException {
    public PacketSendException(String string) {
        super(string);
    }
}
