package client.interfaces;

import lib.model.LibraryBook;

import java.io.IOException;
import java.util.List;

public interface ClientInterface
{
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