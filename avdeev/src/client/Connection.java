package client;

import client.interfaces.ConnectionInterface;
import lib.model.LibraryBook;
import lib.service.BookService;
import lib.service.ConnectionService;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Connection implements ConnectionInterface
{
    private static final int DEFAULT_PORT = 5555;
    private static final String DEFAULT_HOST = "localhost";
    private static final String ARCHIVE_PATH = "avdeev/src/client/data/temp/books.zip";
    private boolean connection = false;

    private Socket socket;

    @Override
    public boolean isConnected()
    {
        return connection;
    }

    @Override
    public int createConnection()
    {
        return createConnection(DEFAULT_HOST, DEFAULT_PORT);
    }

    @Override
    public int createConnection(String host, int port)
    {
        if(connection)
            return 0;
        try
        {
            socket = new Socket(host, port);
            connection = true;
            return 0;
        }
        catch(ConnectException e)
        {
            ClientException.Throw(e);
            return -1;
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
            return 1;
        }
    }

    @Override
    public int closeConnection()
    {
        if(!connection)
            return 0;
        try
        {
            sendCommand("EXIT");
            socket.close();
            connection = false;
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
            return 1;
        }
        return 0;
    }

    @Override
    public String readInputLine() throws IOException
    {
        return ConnectionService.readInputLine(socket);
    }

    @Override
    public int sendCommand(String command) throws IOException
    {
        return ConnectionService.sendCommand(command, socket);
    }

    @Override
    public void sendFile(String path) throws IOException
    {
        ConnectionService.uploadFile(path, socket);
    }

    @Override
    public List<LibraryBook> downloadBooks()
    {
        if(isConnected())
        {
            try
            {
                ConnectionService.sendCommand("GET_BOOKS", socket);
                ConnectionService.downloadFile(ARCHIVE_PATH, socket);
                return BookService.loadBooksFromArchive(ARCHIVE_PATH);
            }
            catch(JAXBException | IOException e)
            {
                ClientException.Throw(e);
            }
        }
        return new ArrayList<>();
    }
}
