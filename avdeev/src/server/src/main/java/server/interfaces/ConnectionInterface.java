package server.interfaces;

import java.io.IOException;

@SuppressWarnings("unused")
public interface ConnectionInterface
{
    /**
     * Method for control for commands/requests
     * @param command   inputStream command
     * @return          run the command's method and return status true/false
     */
    boolean readCommand(String command) throws IOException;

    /**
     * Check inventory number is free or occupied
     * @param inventoryNumber   Inventory number
     * @throws IOException      IOException
     */
    void checkInventoryNumber(int inventoryNumber) throws IOException;

    /**
     * Upload file from server and save it
     * @throws IOException  IOException
     */
    void saveBooks() throws IOException;

    /**
     * Send books to client
     * @throws IOException  IOException
     */
    void getBooks() throws IOException;

    /**
     * Check the books archive existed on server
     */
    void checkBooks() throws IOException;

    /**
     * Check the status to write/save data on server
     */
    void checkStatus() throws IOException;

    /**
     * Free status to write/save data on server
     */
    void freeStatus();

    /**
     * Blocked status to write/save data on server
     */
    void blockedStatus();
}
