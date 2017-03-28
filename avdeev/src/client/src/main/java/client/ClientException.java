package client;

class ClientException
{
    private static final String LOGFILE_DIR = "log";
    private static final String LOGFILE_NAME = "log.txt";
    private static final String LOGFILE_PATH = LOGFILE_DIR + "/" + LOGFILE_NAME;

    private ClientException()
    {}

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static void Throw(Exception exception)
    {
        exception.printStackTrace();
        /*File logDir = new File(LOGFILE_DIR);
        File logFile = new File(LOGFILE_PATH);
        if(!logDir.exists())
            logDir.mkdir();
        if(!logFile.exists())
            try
            {
                logFile.createNewFile();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        try(PrintWriter pw = new PrintWriter(new FileWriter(LOGFILE_PATH, true), true))
        {
            exception.printStackTrace(); // debug mode
            pw.write(exception.getMessage());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }*/
    }
}