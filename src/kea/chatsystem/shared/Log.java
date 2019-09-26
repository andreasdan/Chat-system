package kea.chatsystem.shared;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/***
 * Singleton logging class that prints messages to system console
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public class Log {

    private static Log log;

    //formats the datetime in print outs
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM-yyyy HH:mm:ss");

    /***
     * Constructor with no parameters
     */
    private Log() {}

    /***
     * Singleton get method that returns the only static Log object
     * @return the static Log object initiated
     */
    public static Log getInstance() {
        if (log == null) {
            log = new Log();
        }

        return log;
    }

    /***
     * Prints a message from another client to the console
     * @param text The text message to print
     */
    public void message(String text) {
        write(text);
    }

    /***
     * Prints a transaction message to the console
     * @param text The text to print out
     * @param incoming A true/false value indicating whether or not the transaction is outgoing or incoming
     */
    public void transaction(String text, boolean incoming) {
        String direction;
        if (incoming) {
            direction = "(Incoming)";
        } else {
            direction = "(Outgoing)";
        }

        write("Transaction " + direction + ": " + text);
    }

    /***
     * Prints a message to the console as a debug message
     * @param text The text message to print
     */
    public void debug(String text) {
        write("Debug: " + text);
    }

    /***
     * Prints a message to the console as an info message
     * @param text The text message to print
     */
    public void info(String text) {
        write("Info: " + text);
    }

    /***
     * Prints a message to the console as an error message
     * @param text The text message to print
     */
    public void error(String text) {
        write("Error: " + text);
    }

    /***
     * Prints a message to the console
     * @param text The text message to print
     */
    private void write(String text) {
        System.out.println(Thread.currentThread().getName() + ": " + LocalDateTime.now().format(dateTimeFormatter) + " - " + text);
    }
}
