package server;

import server.interfaces.ConnectionInterface;
import lib.model.LibraryBook;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Connection extends Thread implements ConnectionInterface
{
    private InputStream inputStream;
    private OutputStream outputStream;

    private BufferedReader inputReader;
    private PrintWriter outputWriter;
    private boolean status;
    private List<LibraryBook> books;

    void setBooks(List<LibraryBook> books)
    {
        this.books = books;
    }
    boolean getStatus()
    {
        return status;
    }

    Connection(Socket socket, Boolean status)
    {
        this.status = status;
        try
        {
            inputStream = socket.getInputStream();
            inputReader = new BufferedReader(new InputStreamReader(inputStream));
            outputStream = socket.getOutputStream();
            outputWriter = new PrintWriter(outputStream, true);
        }
        catch(IOException e)
        {
            ServerException.Throw(e);
        }
    }

    /**
     * The run method of this thread.
     */
    public void run()
    {
        String command;
        outputWriter.println("Вы зашли на сервер!");
        try
        {
            // Принимаем сообщения, пока не получем команду @EXIT
            while(!(command = inputReader.readLine()).equalsIgnoreCase("EXIT"))
            {
                if(!readCommand(command))
                    outputWriter.print("ERROR Неизвестная комманда!");
            }
        }
        catch(IOException | NullPointerException e)
        {
            ServerException.Throw(e);
        }
        finally
        {
            status = false;
        }
    }

    // Help method
    private int getNumericArgument(String line)
    {
        String[] arr = line.split(" ");
        if(arr.length != 2)
            throw new IllegalArgumentException(line);
        return Integer.parseInt(arr[1]);
    }

    @Override
    public boolean readCommand(String command) throws IOException
    {
        if(command.startsWith("CHECK_INVENTORY_NUMBER"))
            checkInventoryNumber(getNumericArgument(command));
        if(command.equals("SAVE_BOOKS"))
            saveBooks("avdeev/src/server/data/");
        return false;
    }

    @Override
    public void checkInventoryNumber(int inventoryNumber)
    {
        boolean checker = false;
        for(LibraryBook book: books)
        {
            if(book.getInventoryNumber() == inventoryNumber)
            {
                checker = true;
                break;
            }
        }
        if(checker)
            outputWriter.println("OCCUPIED");
        else
            outputWriter.println("FREE");
    }

    @Override
    public void saveBooks(String path) throws IOException
    {
        File archiveFile = new File(path + "books.zip");
        FileOutputStream fileOutputStream;

        if(!archiveFile.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            archiveFile.createNewFile();
        }

        fileOutputStream = new FileOutputStream(archiveFile);
        int out;
        while((out = inputStream.read()) != -1)
        {
            fileOutputStream.write(out);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
