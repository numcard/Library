package client;

import client.interfaces.ClientInterface;
import client.interfaces.ConnectionInterface;
import shared.model.LibraryBook;
import shared.service.BookService;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Client implements ClientInterface
{
    private final ConnectionInterface connection;

    Client()
    {
        connection = new Connection();
    }

    public ConnectionInterface getConnection()
    {
        return connection;
    }

    @Override
    public void saveBooks(List<LibraryBook> libraryBooks)
    {
        try
        {
            String path = getClass().getResource("../").getPath();
            File archiveFile = BookService.saveBooksToArchive(libraryBooks, path + "data/");
            connection.sendCommand("SAVE_BOOKS");
            connection.sendFile(archiveFile.getPath());
            System.out.println("success");
        }
        catch(IOException | JAXBException e)
        {
            ClientException.Throw(e);
        }
    }

    @Override
    public int checkInventoryNumber(int inventoryNumber)
    {
        try
        {
            connection.sendCommand("CHECK_INVENTORY_NUMBER " + inventoryNumber);
            String answer = connection.readInputLine();
            if(answer.equals("OCCUPIED"))
                return 1;
            if(answer.equals("FREE"))
                return 0;
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
        }
        return -1;
    }
}
