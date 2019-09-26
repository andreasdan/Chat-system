package kea.chatsystem.server;

import kea.chatsystem.server.util.ClientList;
import kea.chatsystem.server.util.MessageList;
import kea.chatsystem.shared.ErrorType;
import kea.chatsystem.shared.Log;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * Worker class that is designed to handle a single client connection in a separate thread
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public class Worker implements Runnable {

    //regex pattern to check for illegal chars in username
    private static Pattern regexPattern = Pattern.compile("[^a-zA-Z0-9_-æøåÆØÅ]+$");

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String username;

    /***
     * Constructor for a worker
     * @param socket The socket that holds the information needed to communicate
     * @throws IOException
     */
    public Worker(Socket socket) throws IOException {
        this.socket = socket;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream());
    }

    /***
     * Method that gets called upon Thread.start() call, which starts the worker thread.
     */
    public void run() {
        try {
            //assume username is illegal
            boolean usernameOK = false;
            String joinMessage;
            if ((joinMessage = input.readLine()) != null) {

                // 6 is the minimum length to have a 1 character username
                if (joinMessage.length() > 5) {
                    if (!joinMessage.substring(0, 4).equals("JOIN")) {
                        output.println("J_ER " + ErrorType.UNEXPECTED_COMMAND);
                        output.flush();
                    } else {
                        username = joinMessage.substring(5).split(",")[0];
                        Matcher matcher = regexPattern.matcher(username);
                        if (username.length() > 12 || matcher.find()) {
                            output.println("J_ER " + ErrorType.ILLEGAL_USERNAME);
                            output.flush();
                        } else {
                            usernameOK = true;
                        }
                    }
                } else {
                    //send join error message back
                    output.println("J_ER " + ErrorType.MISUSED_COMMAND);
                    output.flush();
                }

                //if username is not illegal, attempt to add it
                if (usernameOK) {
                    if (ClientList.getInstance().addClient(username, socket.getInetAddress(), socket.getPort())) {
                        Log.getInstance().info("JOIN OK: Added client with username '" + username + "' to the active list.");

                        //send join ok message to client
                        output.println("J_OK");
                        output.flush();

                        //keep the worker working to receive messages
                        work();
                    } else {
                        output.println("J_ER " + ErrorType.DUPLICATE_USERNAME);
                        output.flush();

                        Log.getInstance().info("JOIN FAILED: Leaving thread as username was already active. (Client must reconnect with a different username)");
                    }
                }
            }
        } catch (IOException ioE) {
            Log.getInstance().error("Client connection stopped unexpectedly: " + ioE.getMessage());
        } catch (InterruptedException iE) {
            Log.getInstance().info("Thread interrupted.. Closing thread..");
        }
    }

    /***
     * Method that gets called once the client connection has been properly set up and verified
     * @throws InterruptedException
     * @throws IOException
     */
    private void work() throws InterruptedException, IOException {
        //run while client still exists in the active client list
        while (ClientList.getInstance().isActive(username)) {
            //read message
            String message;
            if ((message = input.readLine()) != null) {
                //log transaction
                Log.getInstance().transaction(message, true);
                //check if message is more than 4 chars
                if (message.length() < 4) {
                    Log.getInstance().error("Client message is too short to contain any meaningful information. (Minimum is 4 characters)");
                } else {
                    //check client type
                    String type = message.substring(0, 4);
                    switch (type) {
                        case "IMAV":
                            ClientList.getInstance().updateHeartbeat(username); //update heartbeat
                            break;
                        case "DATA":
                            //get the message length minus the DATA : part of the message
                            int actualLength = message.length() - (username.length() + 7);
                            if (actualLength > 0) {
                                //check if message exceeds the 250 char limit (this is also checked in the list object)
                                if (actualLength > 250) {
                                    Log.getInstance().error("Invalid DATA message received. Reason: Message was too long ( > 250 chars )");
                                } else {
                                    String msg = message.substring(7 + username.length());
                                    MessageList.getInstance().addMessage(username, msg);
                                }
                            } else {
                                //if message found to be 0 chars long, simply print out an error message
                                Log.getInstance().error("Invalid DATA message received. Reason: No message was attached.");
                            }
                            break;
                        case "QUIT":
                            ClientList.getInstance().remove(username); //remove on quit message
                            break;
                        default:
                            Log.getInstance().error("Unknown/unexpected client message: '" + message + "'");
                            break;
                    }
                }
            }

            Thread.sleep(200);
        }

        //if the user still exists in the list somehow, then remove it
        ClientList.getInstance().remove(username);

        Log.getInstance().info("Leaving thread because client is no longer active...");
    }
}
