import client.Connection;

import java.io.IOException;

public class TestClient
{
    public static void main(String[] args) throws IOException
    {
        Connection connection = new Connection();
        connection.createConnection();
        for(int i = 0; i < 1000; i++)
            connection.sendCommand("COMMAND " + i);
        connection.closeConnection();
    }
}
