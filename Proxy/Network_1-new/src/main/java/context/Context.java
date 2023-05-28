package main.java.context;

public class Context {
    private String ipGroup;
    private int port;
    private int notifyPeriod;

    public Context(String ipGroup, int port, int notifyPeriod) {
        this.ipGroup = ipGroup;
        this.port = port;
        this.notifyPeriod = notifyPeriod;
    }

    public String getIpGroup() {
        return ipGroup;
    }
    
    public int getPort() {
        return port;
    }
    
    public int getNotifyPeriod() {
        return notifyPeriod;
    }

    public void setIpGroup(String ipGroup) {
        this.ipGroup = ipGroup;
    }    

    public void setPort(int port) {
        this.port = port;
    }

    public void setNotifyPeriod(int notifyPeriod) {
        this.notifyPeriod = notifyPeriod;
    }
}
