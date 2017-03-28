package shared.service;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.UnexpectedException;

public class ConnectionService
{
    /**
     * Read Input Line from Input socket stream
     *
     * @return Input line if success, empty line if there's no connection
     * @throws IOException IOException
     */
    public static String readInputLine(Socket socket) throws IOException
    {
        if(socket.isConnected())
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine();
        }
        else
            throw new SocketException("socket is closed");
    }

    /**
     * Send text command to Output socket stream
     *
     * @param command Text command
     * @param socket  Socket
     * @throws IOException IOException
     */
    public static void sendCommand(String command, Socket socket) throws IOException
    {
        if(socket.isConnected())
        {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(command);
            writer.flush();
        }
        else
            throw new ConnectException("socket is closed");
        System.out.println(command + " sendCommand:finish");
    }

    /**
     * Upload file to Output socket stream
     *
     * @param path   File path to send
     * @param socket Socket
     * @throws IOException IOException
     */
    @SuppressWarnings("Duplicates")
    public static void uploadFile(String path, Socket socket) throws IOException
    {
        File archiveFile = new File(path);
        if(!archiveFile.exists())
            throw new FileNotFoundException(path);

        long fileSize = archiveFile.length();
        // send fileSize and wait answer
        sendCommand("ARCHIVE_SIZE:" + fileSize, socket);
        String answer = readInputLine(socket);
        if(!answer.equals("ARCHIVE_SIZE:" + fileSize))
            throw new UnexpectedException("Unexpected line: " + answer);
        FileInputStream in = new FileInputStream(archiveFile);
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

        for(long i = 0L; i < fileSize; i++)
        {
            int bytes = in.read();
            //System.out.println("send: " + bytes);
            if(bytes == -1)
                throw new IllegalStateException("Input stream is closed");
            out.write(bytes);
        }

        out.flush();
        in.close();
    }

    /**
     * Download file from socket input stream
     *
     * @param path   File's path to save
     * @param socket Socket  @throws IOException  IOException
     */
    @SuppressWarnings("Duplicates")
    public static void downloadFile(String path, Socket socket) throws IOException
    {
        File file = new File(path);
        if(!file.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        }
        System.out.println("wait fileSize");
        String answer = readInputLine(socket);
        long fileSize = Long.parseLong(answer.split(":")[1]);
        // send fileSize in reply to do "handshake"
        sendCommand("ARCHIVE_SIZE:" + fileSize, socket);
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
