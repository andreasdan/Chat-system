package kea.chatsystem.client;

import java.io.IOException;
import java.util.Scanner;

/***
 * Main class for client
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public class Main {

    /***
     * Starting main method
     * @param args Arguments to start the process with (these are ignored)
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Client client = new Client();

        String username;
        String serverIp;
        int port;
        String input;

        System.out.println("*** Chat System ***");
        System.out.println("Type 'JOIN <<username>>, <<serverip>>:<<port>>' to connect with the specified chat server.");

        //while client has not yet joined, try to join by user input parameters
        while (!client.hasJoined()) {
            input = scanner.nextLine();
            //quit if user requests it
            if (input.equalsIgnoreCase("quit")) {
                System.exit(0);
            }

            //check first 5 chars as cmd
            if (!input.substring(0, 5).equals("JOIN ")) {
                System.out.println("You need to join a server first! See the message above...");
            } else {
                try {
                    //split input into username, serverip and port
                    username = input.substring(5).split(",")[0];
                    String serverConfig = input.split(" ")[2];
                    serverIp = serverConfig.split(":")[0];
                    port = Integer.parseInt(serverConfig.split(":")[1]);

                    //attempt to join server
                    client.join(username, serverIp, port);

                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println("JOIN message is incorrectly formatted. Try again.");
                } catch (IOException e) {
                    System.out.println("Could not connect to the specified sever configuration...");
                }
            }
        }

        //this point is reached whenever client has joined
        client.keepRunning();
    }
}
