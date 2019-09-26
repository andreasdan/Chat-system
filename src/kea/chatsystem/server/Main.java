package kea.chatsystem.server;

/***
 * Main class for server
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public class Main {

    /***
     * Main starting point of the server process
     * @param args Arguments to start process with (these are ignored)
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.listen();
    }
}
