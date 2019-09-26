package kea.chatsystem.server.util;

import java.net.InetAddress;

/***
 * Holds the connection information of a given client connected
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public class ClientInfo {

    private InetAddress ipAddress;
    private int port;
    private long lastHeartbeat;

    /***
     * Constructor of client info
     * @param ipAddress The remote ip address of the client
     * @param port The port number of the client
     * @param lastHeartbeat The last heartbeat timestamp
     */
    public ClientInfo(InetAddress ipAddress, int port, long lastHeartbeat) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.lastHeartbeat = lastHeartbeat;
    }

    /***
     * get method
     * @return ip address of the client
     */
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    /***
     * set method
     * @param ipAddress the ip address to set for the client
     */
    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    /***
     * get method
     * @return the port number of the client
     */
    public int getPort() {
        return port;
    }

    /***
     * set method
     * @param port the port number to set for the client
     */
    public void setPort(int port) {
        this.port = port;
    }

    /***
     * get method
     * @return last known heartbeat timestamp as long
     */
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    /***
     * set method
     * @param lastHeartbeat the timestamp to set as last known heartbeat from the client
     */
    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
}
