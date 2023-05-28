package controller.msg;

import exceptions.PacketReceiveException;

public interface MsgReceiver extends Runnable {
    void startReceive() throws PacketReceiveException;

    @Override
    default void run() {
        try {
            startReceive();
        } catch (PacketReceiveException ignored) {
        }
    }
}
