package controller.node.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.net.InetAddress;

@Getter
@Setter
@AllArgsConstructor
public class MasterInfo {
    private int masterId;
    private int masterPort;
    private InetAddress masterIp;
}
