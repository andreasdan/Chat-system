package kea.chatsystem.client;

import kea.chatsystem.shared.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * Client that makes it possible to connect to a chat server
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public class Client {

    private static Pattern regexPattern = Pattern.compile("[^a-zA-Z0-9_-æøåÆØÅ]+$");

    private boolean hasJoined;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;
    private boolean running = true;

    private Scanner scanner;

    /***
     * Constructor with no parameters
     */
    public Client() {
        hasJoined = false;
        scanner = new Scanner(System.in);
    }

    /***
     * Method to get a boolean value indicating the join status of the client
     * @return true if client received a join ok message, otherwise false
     */
    public boolean hasJoined() {
        return hasJoined;
    }

    /***
     * Attempts to join the chat server from the given parameters
     *
     * @param username The chosen username
     * @param serverIp The chosen server ip address
     * @param port The port number on the server to connect
     * @return true if client received a join ok message, otherwise false
     * @throws IOException
     */
    public boolean join(String username, String serverIp, int port) throws IOException {

        //make sure username is not illegal
        Matcher matcher = regexPattern.matcher(username);
        if (username.length() == 0 || username.length() > 12 || matcher.find()) {
            System.out.println("Username '" + username + "' is not valid. Length must be 1-12 characters and contain only a-å, A-Å, 0-9, '-' and '_' characters!");
        } else {
            //initiate i/o variables
            socket = new Socket(serverIp, port);
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //send join request
            transmit("JOIN " + username + ", " + serverIp + ":" + port);

            //get server response and check it
            String response;
            if ((response = reader.readLine()) != null) {
                //get the first 4 letters
                String serverResponse = response.substring(0, 4);
                if (serverResponse.equals("J_OK")) {
                    Log.getInstance().info("Connected to server successfully!");
                    //set username to this and set hasJoined to true
                    this.username = username;
                    hasJoined = true;
                } else if (serverResponse.equals("J_ER")) {
                    Log.getInstance().info("Could not connect to chat server. Reason: '" + response.substring(5) + "'.");
                } else {
                    Log.getInstance().info("Unexpected response from server: '" + response + "'.");
                }
            }
        }

        return hasJoined;
    }

    /***
     * Method to keep the main thread alive be reading user input in console an sending it as chat messages
     */
    public void keepRunning() {

        //return if not joined
        if (!hasJoined) {
            Log.getInstance().error("Cannot run client thread because J_OK was never received!");
            return;
        }

        //create new thread to read data from server
        Thread backgroundReader = new Thread(new BackgroundReader(socket.getLocalPort(), reader));
        backgroundReader.setDaemon(true); //make daemon
        backgroundReader.start();

        //create new thread to send heartbeats
        Thread imavMessenger = new Thread(new ImavMessenger(writer));
        imavMessenger.setDaemon(true); //make daemon
        imavMessenger.start();

        try {

            String input;

            //run in while loop
            while (running) {
                //check if user has entered new input
                if (scanner.hasNextLine()) {
                    input = scanner.nextLine();

                    //if user requests quit, exit the program
                    if (input.equalsIgnoreCase("quit")) {
                        transmit("QUIT");
                        running = false;
                    } else {
                        //treat input as a message with DATA as message
                        transmit("DATA " + username + ": " + input);
                    }
                }

                Thread.sleep(250);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.getInstance().error("Thread was interrupted.. Closing..");
            System.exit(1);
        }
    }

    /***
     * Writes a text message to the server and logs the transaction
     * @param text The message to send
     */
    private void transmit(String text) {
        //print to the stream
        writer.println(text);
        writer.flush();

        //log transaction
        Log.getInstance().transaction(text, false);
    }
}
