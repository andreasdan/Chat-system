package kea.chatsystem.client;

import kea.chatsystem.shared.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/***
 * Background reader to check for incoming data from server
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public class BackgroundReader implements Runnable {

    private int port = 0;
    private BufferedReader reader;

    /***
     * Constructor that creates a background reader to check for incoming data from server
     * @param port The port number to listen on
     * @param reader The BufferedReader to check continuously
     */
    public BackgroundReader(int port, BufferedReader reader) {
        this.port = port;
        this.reader = reader;
    }

    /***
     * Run method that gets executed then Thread.start() is called
     */
    public void run() {

        Thread.currentThread().setName("BackgroundReader");

        try {

            String message;
            byte[] buffer;
            DatagramSocket datagramSocket = new DatagramSocket(port);

            //if input socket is not closed keep running
            while (!datagramSocket.isClosed()) {
                buffer = new byte[269]; //269 = max possible length (7 from protocol, 12 from username limit, 250 from message limit 7+12+250=269)
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packet); //blocks thread until a message is received

                //read message
                message = new String(packet.getData(), 0, packet.getLength());

                //log incoming message
                Log.getInstance().transaction(message, true);

                //separate first 4 chars as protocol message type
                String type = message.substring(0, 4);
                switch (type) {
                    case "DATA":
                        Log.getInstance().message(message.substring(5));
                        break;
                    case "LIST":
                        Log.getInstance().message("List of active users: " + message.substring(5));
                        break;
                    default:
                        Log.getInstance().info("Unknown message received: '" + message + "'");
                        break;
                }

                //sleep 250ms
                Thread.sleep(250);
            }
        } catch (IOException ioE) {
            Log.getInstance().error("IOException: " + ioE.getMessage());
        } catch (InterruptedException iE) {
            Log.getInstance().error("Thread was interrupted..");
        } finally {
            Log.getInstance().info("Closing thread..");
        }
    }
}