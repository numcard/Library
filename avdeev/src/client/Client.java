package client;

import lib.service.BookService;
import lib.model.LibraryBook;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;

public class Client
{
    private static final int DEFAULT_PORT = 5555;
    private static final String DEFAULT_HOST = "localhost";
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean connected = false;

    public boolean isConnected()
    {
        return connected;
    }

    public int createConnection()
    {
        return createConnection(DEFAULT_HOST, DEFAULT_PORT);
    }
    private int createConnection(String host, int port)
    {
        try
        {
            socket = new Socket(host, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            connected = true;
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

    /**
     * Command to save books on server side
     * @param libraryBooks  Book's list
     */
    public void saveBooks(List<LibraryBook> libraryBooks)
    {
        try
        {
            File archiveFile = BookService.saveBooksToArchive(libraryBooks, "avdeev/src/client/data/temp/");
            sendCommand("SAVE_BOOKS");
            sendFile(archiveFile);
        }
        catch(IOException | JAXBException e)
        {
            ClientException.Throw(e);
        }
    }

    /**
     * Command to close connection
     */
    public void closeConnection()
    {
        try
        {
            sendCommand("EXIT");
            socket.close();
            connected = false;
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
        }
    }

    /**
     * Command to check inventory number existed
     * @param inventoryNumber   inventory number
     * @return                  -1 if answer is incorrect or has exception
     */
    public int checkInventoryNumber(int inventoryNumber)
    {
        sendCommand("CHECK_INVENTORY_NUMBER " + inventoryNumber);
        String answer = readInputLine();
        if(answer.equals("OCCUPIED"))
            return 1;
        if(answer.equals("FREE"))
            return 0;
        return -1;
    }

    // help method
    public String readInputLine()
    {
        BufferedReader reader;
        if(connected)
        {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            try
            {
                return reader.readLine();
            }
            catch(IOException e)
            {
                ClientException.Throw(e);
                return "";
            }
        }
        return "";
    }

    // help method
    private void sendCommand(String command)
    {
        PrintWriter writer;
        if(connected)
        {
            writer = new PrintWriter(outputStream, true);
            writer.println(command);
        }
    }

    // help method
    private void sendFile(File file) throws IOException
    {
        FileInputStream fileInputStream = new FileInputStream(file);
        int in;
        while((in = fileInputStream.read()) != -1)
        {
            outputStream.write(in);
        }
        outputStream.flush();
    }
}
