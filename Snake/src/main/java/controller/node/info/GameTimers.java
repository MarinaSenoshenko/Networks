package controller.node.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Timer;

@Getter
@Setter
@AllArgsConstructor
public class GameTimers {
    private Timer modelUpdater;
    private Timer announceSender;
    private Timer msgsResender;
    private Timer aliveChecker;
}
