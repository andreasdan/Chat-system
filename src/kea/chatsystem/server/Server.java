package kea.chatsystem.server;

import kea.chatsystem.shared.ErrorType;
import kea.chatsystem.shared.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/***
 * Server class that manages the server part of the client/server chat system
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public class Server {

    private static final int PORT = 1234;
    private static final int MAX_THREADS = 5;
    private ThreadPoolExecutor threadPoolExecutor;

    /***
     * Constructor with no parameters
     */
    public Server() {
        //initiate thread pool with static max threads count
        threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(MAX_THREADS);
    }

    /***
     * Listen method that opens the server socket and initiates thread pool and watcher thread
     */
    public void listen() {
        try {
            Log.getInstance().info("Starting server socket..");
            ServerSocket serverSocket = new ServerSocket(PORT);

            //create client watcher daemon thread
            Log.getInstance().debug("Creating new daemon watcher thread..");
            Thread watcher = new Thread(new Watcher());
            watcher.setDaemon(true); //daemon thread (low priority)
            watcher.start();
            Log.getInstance().info("Watcher thread started successfully.");

            Socket socket;
            while (true) {
                socket = serverSocket.accept(); //this method blocks the thread until a new connection is made

                //if no threads from pool are available inform the client
                if (threadPoolExecutor.getActiveCount() == MAX_THREADS) {
                    PrintWriter writer = new PrintWriter(socket.getOutputStream());
                    writer.println("J_ER " + ErrorType.NO_AVAILABLE_THREAD);
                    Log.getInstance().transaction("J_ER " + ErrorType.NO_AVAILABLE_THREAD, false);
                    writer.flush();

                    //clean up
                    writer.close();
                    socket.close();

                } else {
                    Log.getInstance().debug("Accepting new client connection.");

                    //execute the thread from the thread pool
                    Thread clientThread = new Thread(new Worker(socket));
                    threadPoolExecutor.execute(clientThread);

                    Log.getInstance().debug("Created new thread to handle client connection.");
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.getInstance().error("Closing caused by IOException..");
            System.exit(1);
        }
    }
}
