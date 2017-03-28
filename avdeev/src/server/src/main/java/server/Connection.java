package server;

import server.interfaces.ConnectionInterface;
import shared.model.LibraryBook;
import shared.service.BookService;
import shared.service.ConnectionService;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class Connection implements Runnable, ConnectionInterface
{
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
                    System.out.println(command);
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
            List<LibraryBook> books = BookService.loadBooksFromArchive();
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
        catch(FileNotFoundException e)
        {
            freeFlag = true;
        }

        if(freeFlag)
            ConnectionService.sendCommand("FREE", socket);
        else
            ConnectionService.sendCommand("OCCUPIED", socket);
    }

    @Override

    public void saveBooks() throws IOException
    {
        BookService.createFiles();
        ConnectionService.downloadFile(BookService.BOOK_ARCHIVE_PATH, socket);
    }

    @Override
    public void getBooks() throws IOException
    {
        ConnectionService.uploadFile(BookService.BOOK_ARCHIVE_PATH, socket);
    }

    @Override
    public void checkBooks() throws IOException
    {
        File file = new File(BookService.BOOK_ARCHIVE_PATH);
        if(!file.exists())
            ConnectionService.sendCommand("NOT_EXISTED", socket);
        else
        {
            try(ZipFile zipFile = new ZipFile(file))
            {
                if(zipFile.size() == 0)
                    ConnectionService.sendCommand("NOT_EXISTED", socket);
                else
                    ConnectionService.sendCommand("EXISTED", socket);
            }
            catch(ZipException e)
            {
                ConnectionService.sendCommand("NOT_EXISTED", socket);
            }
        }
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
