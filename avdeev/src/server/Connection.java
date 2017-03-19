package server;

import lib.service.ConnectionService;
import server.interfaces.ConnectionInterface;
import lib.model.LibraryBook;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Future;

public class Connection implements Runnable, ConnectionInterface
{
    private static final String ARCHIVE_PATH = "avdeev/src/server/data/books.zip";
    private Socket socket;
    private List<LibraryBook> books;
    private Future<?> future;

    void setBooks(List<LibraryBook> books)
    {
        this.books = books;
    }
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
        if(command.startsWith("GET_BOOKS"))
            getBooks();
        if(command.startsWith("CHECK_INVENTORY_NUMBER"))
            checkInventoryNumber(getNumericArgument(command));
        if(command.equals("SAVE_BOOKS"))
            saveBooks();
        return false;
    }

    @Override
    public void checkInventoryNumber(int inventoryNumber) throws IOException
    {
        boolean checker = false;
        for(LibraryBook book: books)
        {
            if(book.getInventoryNumber() == inventoryNumber)
            {
                checker = true;
                break;
            }
        }
        if(checker)
            ConnectionService.sendCommand("OCCUPIED", socket);
        else
            ConnectionService.sendCommand("FREE", socket);
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
}
