package kea.chatsystem.server;

import kea.chatsystem.server.util.ClientInfo;
import kea.chatsystem.server.util.ClientList;
import kea.chatsystem.server.util.MessageList;
import kea.chatsystem.shared.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

/***
 * Watcher class that automatically removes inactive clients, broadcasts chat messages and re-sends list of active users whenever it changes
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public class Watcher implements Runnable {

    /***
     * Constructor with no parameters
     */
    public Watcher() { }

    /***
     * Method that gets executed upon Thread.start() call
     */
    public void run() {

        Thread.currentThread().setName("Watcher");

        try {
            DatagramPacket datagramPacket;
            DatagramSocket datagramSocket = new DatagramSocket(1235);
            String message;
            int clientsRemoved;
            Set<String> lastKnownClientList = new HashSet<>();

            //run always unless the process gets terminated
            while (true) {
                Thread.sleep(200); //sleep 150 ms

                //broadcast messages if any new ones are in the list
                while ((message = MessageList.getInstance().getNextMessage()) != null) {
                    //make byte array from message
                    byte[] bMessage = message.getBytes();
                    //send datagram packet to all users
                    for (ClientInfo info : ClientList.getInstance().getClientInfoList()) {
                        datagramPacket = new DatagramPacket(bMessage, bMessage.length, info.getIpAddress(), info.getPort());
                        datagramSocket.send(datagramPacket);
                    }
                }

                //remove inactive clients, log the number of removed clients if it is more than 0
                if ((clientsRemoved = ClientList.getInstance().removeInactiveClients()) > 0) {
                    Log.getInstance().info("Removed " + clientsRemoved + " client(s) that stopped sending heartbeats..");
                }

                //send list of clients if it has changed since last time
                Set<String> currentSet = ClientList.getInstance().getUsernameList();
                if (!currentSet.containsAll(lastKnownClientList) || currentSet.size() != lastKnownClientList.size()) {

                    //clear the list of known clients then add all the current ones
                    lastKnownClientList.clear();
                    lastKnownClientList.addAll(currentSet);

                    //make list into byte array
                    String newList = "LIST";
                    for (String username : lastKnownClientList) {
                        newList += " " + username;
                    }
                    byte[] bNewList = newList.getBytes();

                    //send datagram packet to all users with new list
                    for (ClientInfo info : ClientList.getInstance().getClientInfoList()) {
                        datagramPacket = new DatagramPacket(bNewList, bNewList.length, info.getIpAddress(), info.getPort());
                        datagramSocket.send(datagramPacket);
                    }
                }
            }
        } catch (InterruptedException e) {
            Log.getInstance().info("Watcher thread was interrupted...");
        } catch (SocketException sE) {
            Log.getInstance().error("SocketException: " + sE.getMessage());
        } catch (IOException ioE) {
            Log.getInstance().error("IOException: " + ioE.getMessage());
        }
    }
}