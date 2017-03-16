package client.interfaces;

import java.io.IOException;

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
     * @return
     */
    int sendCommand(String command);
}
