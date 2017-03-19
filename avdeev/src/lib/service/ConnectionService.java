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
        if(!archiveFile.exists())
            throw new FileNotFoundException(path);

        long fileSize = archiveFile.length();
        sendCommand("ARCHIVE_SIZE:" + fileSize, socket);
        FileInputStream in = new FileInputStream(archiveFile);
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
        int bytes;
        while((bytes = in.read()) != -1)
        {
            //System.out.println("send: " + bytes);
            out.write(bytes);
        }
        out.flush();
        in.close();

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
        long fileSize = Long.parseLong(readInputLine(socket).split(":")[1]);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

        for(long i = 0L; i < fileSize; i++)
        {
            int bytes = in.read();
            //System.out.println("upload: " + bytes);
            if(bytes == -1)
                throw new IllegalStateException("Input stream is closed");
            out.write(bytes);
        }
        out.flush();
        out.close();
    }
}
