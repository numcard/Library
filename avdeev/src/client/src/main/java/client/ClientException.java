package client;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class ClientException
{
    private static final String LOGFILE_PATH = "/log/log.txt";

    private ClientException()
    {}

    static void Throw(Exception exception)
    {
        try(PrintWriter pw = new PrintWriter(new FileWriter(LOGFILE_PATH, true), true))
        {
            exception.printStackTrace(); // debug mode
            pw.write(exception.getMessage());
        }
        catch(IOException ignored)
        {
            //ignored
        }
    }
}