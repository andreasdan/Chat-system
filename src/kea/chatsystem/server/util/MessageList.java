package kea.chatsystem.server.util;

import java.util.ArrayList;
import java.util.List;

/***
 * Singleton class to hold chat messages
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public class MessageList {

    private static MessageList messageList;

    private List<String> messages;

    /***
     * Constructor with no parameters
     */
    private MessageList() {
        messages = new ArrayList<>();
    }

    /***
     * Synschronized get message to avoid more than one instance
     * @return The MessageList object
     */
    public static MessageList getInstance() {
        if (messageList == null) {
            synchronized (MessageList.class) {
                //double check null value to be sure
                if (messageList == null) {
                    messageList = new MessageList();
                }
            }
        }

        return messageList;
    }

    /***
     * Adds a message to message list
     * @param username Username of the message author
     * @param message The message in plain text
     * @return true if the message was added, false if not
     */
    public boolean addMessage(String username, String message) {
        //make sure no message above 250 chars is added
        if (message.length() > 250) {
            return false;
        } else {
            messages.add("DATA " + username + ": " + message);
            return true;
        }
    }

    /***
     * Method to get the next chat message in the collection
     * @return A string representing the next chat message. Null is returned if no message exists.
     */
    public String getNextMessage() {
        if (messages.size() > 0) {
            String message = messages.get(0); //broadcast the first message to enter the queue
            messages.remove(0); //remove the message
            return message;
        } else {
            return null;
        }
    }
}
