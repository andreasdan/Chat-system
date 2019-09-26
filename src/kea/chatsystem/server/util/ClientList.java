package kea.chatsystem.server.util;

import java.net.InetAddress;
import java.util.*;

/***
 * Singleton class to hold active clients in a collection
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public class ClientList {

    private static ClientList clientList;

    //username, client info
    private Map<String, ClientInfo> clients;

    /***
     * Constructor with no parameters
     */
    private ClientList() {
        clients = new HashMap<>();
    }

    /***
     * Synchronized singleton get to avoid more than one instance
     * @return The static client list
     */
    public static ClientList getInstance() {
        if (clientList == null) {
            synchronized (ClientList.class) {
                //double check null value to make sure
                if (clientList == null) {
                    clientList = new ClientList();
                }
            }
        }

        return clientList;
    }

    /***
     * Update the last heartbeat timestamp for a client
     * @param username The username of the client
     * @return true if the client exists and the value was updated, false if not
     */
    public boolean updateHeartbeat(String username) {
        if (clients.containsKey(username)) {
            //get client from map
            ClientInfo clientInfo = clients.get(username);
            //update heartbeat
            clientInfo.setLastHeartbeat(System.currentTimeMillis());
            //replace the new client with old one in the map
            clients.replace(username, clientInfo);
            return true;
        } else {
            return false;
        }
    }

    /***
     * Synchronized method to add a client to the collection. Synchronized, to prevent two clients added with the same username at the same time
     * @param username The username of the client
     * @param ipAddress The ip address of the client
     * @param port The port number of the client
     * @return true if the client was successfully added, false if not
     */
    public synchronized boolean addClient(String username, InetAddress ipAddress, int port) {
        //check if its a unique username
        boolean uniqueUsername = !clients.containsKey(username);

        //if username found to be unique, add it
        if (uniqueUsername) {
            clients.put(username, new ClientInfo(ipAddress, port, System.currentTimeMillis()));
        }

        return uniqueUsername;
    }

    /***
     * Removes a client from the collection
     * @param username Username of the client to remove
     */
    public void remove(String username) {
        clients.remove(username);
    }

    /***
     * Method to determine if a client is active
     * @param username Username of a given client
     * @return true if the username exists in collection, false if not
     */
    public boolean isActive(String username) {
        //the client is active if it exists in the map
        return clients.containsKey(username);
    }

    /***
     * get method
     * @return a set collection containing the usernames currently in the active client list
     */
    public Set<String> getUsernameList() {
        return clients.keySet();
    }

    /***
     * get method
     * @return a list of ClientInfo objects that are currently in the active client list
     */
    public List<ClientInfo> getClientInfoList() {
        return new ArrayList<>(clients.values());
    }

    /***
     * Method to automatically remove clients that are no longe active
     * @return A number representing how many clients were removed
     */
    public int removeInactiveClients() {
        //create counter
        int counter = 0;

        //synchronize the removal
        synchronized (ClientList.class) {
            for (Map.Entry<String, ClientInfo> entry : clients.entrySet()) {
                //65000 ms is 5 seconds after the last heartbeat should have been received (after a minute) (tolerance is then 5 seconds)
                if (System.currentTimeMillis() - entry.getValue().getLastHeartbeat() > 65000) {
                    clients.remove(entry.getKey());
                    counter++;
                }
            }
        }

        //return number of clients removed
        return counter;
    }
}
