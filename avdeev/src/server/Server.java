package server;

import lib.model.LibraryBook;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server
{
    private static final int DEFAULT_PORT = 5555;
    private List<LibraryBook> books = new ArrayList<>();

    public void start()
    {
        try(ServerSocket listener = new ServerSocket(DEFAULT_PORT))
        {
            while(true)
            {
                Socket socket = listener.accept();
                System.out.println("Клиент зашел!");
                Connection connection = new Connection(socket, true);
                connection.start();
                connection.setBooks(books);
                // Wait client working
                while(connection.getStatus())
                {
                    Thread.sleep(1000);
                }
                connection.interrupt();
                System.out.println("Клиент вышел!");
            }
        }
        catch(IOException | InterruptedException e)
        {
            ServerException.Throw(e);
        }
    }
}
