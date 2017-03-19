package server.interfaces;

import java.io.IOException;

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
}
