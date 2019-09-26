package kea.chatsystem.client;

import kea.chatsystem.shared.Log;

import java.io.PrintWriter;

/***
 * Sends IMAV message heartbeats to the server
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public class ImavMessenger implements Runnable {

    private PrintWriter writer;

    /***
     * Constructor
     * @param writer The PrintWriter that holds the output stream to the server
     */
    public ImavMessenger(PrintWriter writer) {
        this.writer = writer;
    }

    /***
     * Method the gets executed on Thread.start() call
     */
    public void run() {

        Thread.currentThread().setName("ImavMessenger");

        try {
            while (writer != null) {
                //print IMAV heartbeat to the stream
                writer.println("IMAV");
                writer.flush();

                //log transaction
                Log.getInstance().transaction("IMAV", false);

                //sleep 1 minute then repeat
                Thread.sleep(60000);
            }
        } catch (InterruptedException iE) {
            Log.getInstance().info("Thread was interrupted..");
        } finally {
            Log.getInstance().info("Closing thread..");
        }
    }
}
