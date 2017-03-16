package client;

import client.interfaces.ClientInterface;
import client.interfaces.ConnectionInterface;
import lib.model.LibraryBook;
import java.util.List;

public class Client implements ClientInterface
{
    private ConnectionInterface connection;

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
        //TODO
        /*try
        {
            File archiveFile = BookService.saveBooksToArchive(libraryBooks, "avdeev/src/client/data/temp/");
            sendCommand("SAVE_BOOKS");
            sendFile(archiveFile);
        }
        catch(IOException | JAXBException e)
        {
            ClientException.Throw(e);
        }*/
    }

    @Override
    public int checkInventoryNumber(int inventoryNumber)
    {
        //TODO
        /*sendCommand("CHECK_INVENTORY_NUMBER " + inventoryNumber);
        try
        {
            String answer = readInputLine();
            if(answer.equals("OCCUPIED"))
                return 1;
            if(answer.equals("FREE"))
                return 0;
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
        }*/
        return -1;
    }
}
