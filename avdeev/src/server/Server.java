package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server
{
    private static final int DEFAULT_PORT = 5555;
    // indicate editable state of archive file
    private final AtomicInteger status = new AtomicInteger(0);

    public void start()
    {
        ExecutorService executorService = Executors.newWorkStealingPool();
        try(ServerSocket listener = new ServerSocket(DEFAULT_PORT))
        {
            //noinspection InfiniteLoopStatement
            while(true)
            {
                Socket socket = listener.accept();
                Connection connection = new Connection(socket);
                connection.setFuture(executorService.submit(connection));
                connection.setStatus(status);
            }
        }
        catch(IOException e)
        {
            ServerException.Throw(e);
        }
    }
}
