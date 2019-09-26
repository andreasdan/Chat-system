package kea.chatsystem.shared;

/***
 * ErrorTypes used as a part of the protocol between client and server.
 *
 * @author Andreas Dan Petersen
 * @version 1.0
 * @since 25-09-2019
 */
public enum ErrorType {

    NO_AVAILABLE_THREAD(0, "No available thread"),
    ILLEGAL_USERNAME(1, "Illegal username"),
    DUPLICATE_USERNAME(2, "Duplicate username"),
    MISUSED_COMMAND(3, "Misused command"),
    UNEXPECTED_COMMAND(4, "Unexpected command");

    private int id;
    private String error;

    /***
     * Constructor of an error type
     * @param id The error code as id
     * @param error The error message
     */
    ErrorType(int id, String error)
    {
        this.id = id;
        this.error = error;
    }

    /***
     * get method
     * @return the id associated with the error type
     */
    public int getId() {
        return id;
    }

    /***
     * toString method
     * @return the error type as a string with error code and error message
     */
    @Override
    public String toString()
    {
        return id + ": " + error;
    }
}
