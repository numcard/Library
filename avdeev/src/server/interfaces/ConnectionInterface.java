package server.interfaces;

import java.io.IOException;

public interface ConnectionInterface
{
    /**
     * Method for inputStream commands/requests
     * @param command   inputStream command
     * @return          run the command's method and return status true/false
     */
    public boolean readCommand(String command) throws IOException;

    public void checkInventoryNumber(int inventoryNumber);

    /**
     * Read zipArchive from InputStream and Save it
     * @throws IOException  I/0 Exception
     */
    public void saveBooks(String path) throws IOException;
}
