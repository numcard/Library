package server;

import lib.model.LibraryBook;
import lib.service.BookService;
import lib.service.ConnectionService;
import server.interfaces.ConnectionInterface;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Connection implements Runnable, ConnectionInterface
{
    private static final String ARCHIVE_PATH = "avdeev/src/server/data/books.zip";
    private final Socket socket;
    private Future<?> future;
    private AtomicInteger status;

    void setFuture(Future<?> future)
    {
        this.future = future;
    }
    void setStatus(AtomicInteger status)
    {
        this.status = status;
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
        if(command.startsWith("CHECK_STATUS"))
        {
            checkStatus();
            return true;
        }
        if(command.startsWith("FREE_STATUS"))
        {
            freeStatus();
            return true;
        }
        if(command.startsWith("BLOCKED_STATUS"))
        {
            blockedStatus();
            return true;
        }
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
            assert books != null;
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

    @Override
    public void checkStatus() throws IOException
    {
        if(status.get() == 0)
            ConnectionService.sendCommand("FREE_STATUS", socket);
        else
            ConnectionService.sendCommand("BLOCKED_STATUS", socket);
    }

    @Override
    public void freeStatus()
    {
        status.compareAndSet(1, 0);
    }

    @Override
    public void blockedStatus()
    {
        status.compareAndSet(0, 1);
    }
}
