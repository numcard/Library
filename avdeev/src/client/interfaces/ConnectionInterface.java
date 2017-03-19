package client.interfaces;

import lib.model.LibraryBook;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ConnectionInterface
{
    /**
     * Check state connection
     * @return true/false if connected/disconnected
     */
    boolean isConnected();

    /**
     * Create default connection
     * @return 0 if success, -1 if ConnectException, 1 if IOException
     */
    int createConnection();

    /**
     * Create configure connection
     * @param host  hostname
     * @param port  port number
     * @return 0 if success, -1 if ConnectException, 1 if IOException
     */
    int createConnection(String host, int port);

    /**
     * Close connection
     * @return  0 if success, 1 if IOException
     */
    int closeConnection();

    /**
     * Read Input Line from Server
     * @return Input line if success, empty line if there's no connection
     * @throws IOException  IOException
     */
    String readInputLine() throws IOException;

    /**
     * Send text command to Server
     * @param command   Text command
     * @return 0 if success, -1 if there's no connection
     * @throws IOException IOException
     */
    int sendCommand(String command) throws IOException;

    /**
     * Send file to Server
     * @param path          File path to send
     * @throws IOException  IOException
     */
    void sendFile(String path) throws IOException;

    /**
     * Download books from Server
     * @return  List of LibraryBooks
     */
    List<LibraryBook> downloadBooks();

    /**
     * Check the server has a books' archive
     * @return  true if file existed, false if no
     */
    boolean checkBookIsExisted();
}
