package client;

import client.interfaces.ConnectionInterface;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

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
        BufferedReader reader;
        if(isConnected())
        {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine();
        }
        return "";
    }

    @Override
    public int sendCommand(String command)
    {
        PrintWriter writer;
        if(connection)
        {
            try
            {
                writer = new PrintWriter(socket.getOutputStream(), true);
            }
            catch(IOException e)
            {
                ClientException.Throw(e);
                return 1;
            }
            writer.println(command);
            return 0;
        }
        return -1;
    }

    // help method
    private void sendFile(File file) throws IOException
    {
        FileInputStream fileInputStream = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();
        int in;
        while((in = fileInputStream.read()) != -1)
        {
            out.write(in);
        }
        out.flush();
    }
}
