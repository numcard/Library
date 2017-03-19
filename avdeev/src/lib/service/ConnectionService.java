package lib.service;

import java.io.*;
import java.net.Socket;

public class ConnectionService
{
    /**
     * Read Input Line from Input socket stream
     * @return Input line if success, empty line if there's no connection
     * @throws IOException  IOException
     */
    public static String readInputLine(Socket socket) throws IOException
    {
        BufferedReader reader;
        if(socket.isConnected())
        {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine();
        }
        return "";
    }

    /**
     * Send text command to Output socket stream
     * @param command   Text command
     * @param socket    Socket
     * @return  0 if success, -1 if there's no connection
     * @throws IOException IOException
     */
    public static int sendCommand(String command, Socket socket) throws IOException
    {
        PrintWriter writer;
        if(socket.isConnected())
        {
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(command);
            return 0;
        }
        return -1;
    }

    /**
     * Upload file to Output socket stream
     * @param path      File path to send
     * @param socket    Socket
     * @throws IOException IOException
     */
    public static void uploadFile(String path, Socket socket) throws IOException
    {
        File archiveFile = new File(path);
        if(archiveFile.exists())
        {
            FileInputStream in = new FileInputStream(archiveFile);
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            int bytes;
            while((bytes = in.read()) != -1)
            {
                out.write(bytes);
            }
            // send -1 to exit
            out.write(-1);
            out.flush();
            in.close();
        }
    }

    /**
     * Download file from socket input stream
     * @param path          File path to save
     * @param socket        Socket
     * @throws IOException  IOException
     */
    public static void downloadFile(String path, Socket socket) throws IOException
    {
        File file = new File(path);
        if(!file.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        }
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

        int bytes;
        // TODO
        // waiting 255 instead -1
        while((bytes = in.read()) != 255)
        {
            out.write(bytes);
        }
        out.flush();
        out.close();
    }
}
