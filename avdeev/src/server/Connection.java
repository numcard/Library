package server;

import lib.service.BookService;
import lib.service.ConnectionService;
import server.interfaces.ConnectionInterface;
import lib.model.LibraryBook;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Future;

public class Connection implements Runnable, ConnectionInterface
{
    private static final String ARCHIVE_PATH = "avdeev/src/server/data/books.zip";
    private Socket socket;
    private Future<?> future;

    void setFuture(Future<?> future)
    {
        this.future = future;
    }

    Connection(Socket socket)
    {
        this.socket = socket;
    }

    // Help method
    private int getNumericArgument(String line)
    {
        String[] arr = line.split(" ");
        if(arr.length != 2)
            throw new IllegalArgumentException(line);
        return Integer.parseInt(arr[1]);
    }

    @Override
    public void run()
    {
        String command;
        try
        {
            ConnectionService.sendCommand("Вы зашли на сервер!", socket);
            // Принимаем сообщения, пока не получем команду @EXIT
            while(!(command = ConnectionService.readInputLine(socket)).equalsIgnoreCase("EXIT"))
            {
                if(!readCommand(command))
                    ConnectionService.sendCommand("ERROR Неизвестная комманда!", socket);
            }
        }
        catch(IOException e)
        {
            ServerException.Throw(e);
        }
        finally
        {
            future.cancel(true);
        }
    }

    @Override
    public boolean readCommand(String command) throws IOException
    {
        if(command.startsWith("CHECK_BOOKS"))
        {
            checkBooks();
            return true;
        }
        if(command.startsWith("GET_BOOKS"))
        {
            getBooks();
            return true;
        }
        if(command.startsWith("CHECK_INVENTORY_NUMBER"))
        {
            checkInventoryNumber(getNumericArgument(command));
            return true;
        }
        if(command.equals("SAVE_BOOKS"))
        {
            saveBooks();
            return true;
        }
        return false;
    }

    @Override
    public void checkInventoryNumber(int inventoryNumber) throws IOException
    {
        boolean freeFlag = true;
        try
        {
            List<LibraryBook> books = BookService.loadBooksFromArchive(ARCHIVE_PATH);
            for(LibraryBook book: books)
            {
                if(book.getInventoryNumber() == inventoryNumber)
                {
                    freeFlag = false;
                    break;
                }
            }
        }
        catch(JAXBException e)
        {
            ServerException.Throw(e);
        }

        if(freeFlag)
            ConnectionService.sendCommand("FREE", socket);
        else
            ConnectionService.sendCommand("OCCUPIED", socket);
    }

    @Override
    public void saveBooks() throws IOException
    {
        ConnectionService.downloadFile(ARCHIVE_PATH, socket);
    }

    @Override
    public void getBooks() throws IOException
    {
        ConnectionService.uploadFile(ARCHIVE_PATH, socket);
    }

    @Override
    public void checkBooks() throws IOException
    {
        if(new File(ARCHIVE_PATH).exists())
            ConnectionService.sendCommand("EXISTED", socket);
        else
            ConnectionService.sendCommand("NOT_EXISTED", socket);
    }
}
