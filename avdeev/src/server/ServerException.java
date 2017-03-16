package server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class ServerException
{
    private static final String OUTPUT_PATH = "avdeev/src/server/log/log.txt";

    private ServerException()
    {}

    static void Throw(Exception exception)
    {
        try(PrintWriter pw = new PrintWriter(new FileWriter(OUTPUT_PATH, true), true))
        {
            exception.printStackTrace(); // debug mode
            pw.write(exception.getMessage());
        }
        catch(IOException e)
        {
            //TODO
        }
    }
}