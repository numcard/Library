package client.interfaces;

import lib.model.LibraryBook;
import java.util.List;

public interface ClientInterface
{
    /**
     * Returns connection interface to server
     * @return  ConnectionInterface
     */
    ConnectionInterface getConnection();

    /**
     * Command to save books on server side
     * @param libraryBooks  Book's list
     */
    void saveBooks(List<LibraryBook> libraryBooks);

    /**
     * Command to check inventory number existed
     * @param inventoryNumber   inventory number
     * @return                  1 if occupied, 0 if free, -1 if exception/error
     */
    int checkInventoryNumber(int inventoryNumber);
}