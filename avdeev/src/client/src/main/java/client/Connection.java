package client;

import client.interfaces.ConnectionInterface;
import shared.model.LibraryBook;
import shared.service.BookService;
import shared.service.ConnectionService;

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
    public void closeConnection()
    {
        try
        {
            sendCommand("EXIT");
            socket.close();
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
        }
        connection = false;
    }

    @Override
    public String readInputLine() throws IOException
    {
        return ConnectionService.readInputLine(socket);
    }

    @Override
    public void sendCommand(String command) throws IOException
    {
        ConnectionService.sendCommand(command, socket);
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
                ConnectionService.downloadFile(BookService.BOOK_ARCHIVE_PATH, socket);
                return BookService.loadBooksFromArchive();
            }
            catch(JAXBException | IOException e)
            {
                ClientException.Throw(e);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public boolean checkBookIsExisted()
    {
        if(!isConnected())
            throw new IllegalStateException("There's no connection with server");
        try
        {
            ConnectionService.sendCommand("CHECK_BOOKS", socket);
            String answer = ConnectionService.readInputLine(socket);
            if(answer.startsWith("EXISTED"))
                return true;
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
        }
        return false;
    }

    @Override
    public boolean checkStatus()
    {
        try
        {
            ConnectionService.sendCommand("CHECK_STATUS", socket);
            String answer = ConnectionService.readInputLine(socket);
            if(answer.startsWith("FREE_STATUS"))
                return true;
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
        }
        return false;
    }

    @Override
    public void freeStatus()
    {
        try
        {
            ConnectionService.sendCommand("FREE_STATUS", socket);
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
        }
    }

    @Override
    public void blockedStatus()
    {
        try
        {
            ConnectionService.sendCommand("BLOCKED_STATUS", socket);
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
        }
    }
}
