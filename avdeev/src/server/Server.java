package server;

import lib.model.LibraryBook;
import lib.service.BookService;
import lib.service.ConnectionService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
    private static final int DEFAULT_PORT = 5555;

    public void start()
    {
        ExecutorService executorService = Executors.newWorkStealingPool();
        try(ServerSocket listener = new ServerSocket(DEFAULT_PORT))
        {
            while(true)
            {
                Socket socket = listener.accept();
                Connection connection = new Connection(socket);
                connection.setFuture(executorService.submit(connection));
            }
        }
        catch(IOException e)
        {
            ServerException.Throw(e);
        }
    }
}
