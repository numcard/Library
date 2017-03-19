package lib.service;

import lib.model.LibraryBook;
import lib.model.LibraryBookWrapper;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class BookService
{
    /**
     * Load LibraryBooks from XML file and return List of LibraryBooks
     * @param file                      XML file
     * @return                          List of LibraryBook
     * @throws FileNotFoundException    If file not found
     * @throws JAXBException            If Structure of XML file has wrong format
     */
    private static List<LibraryBook> loadBooks(File file) throws FileNotFoundException, JAXBException
    {
        if(!file.exists())
            throw new FileNotFoundException(file.getAbsolutePath());
        JAXBContext context = JAXBContext.newInstance(LibraryBookWrapper.class);
        Unmarshaller um = context.createUnmarshaller();

        // Чтение XML из файла и демаршализация.
        LibraryBookWrapper wrapper = (LibraryBookWrapper) um.unmarshal(file);

        List<LibraryBook> books = new ArrayList<>();
        if(wrapper.getLibraryBooks() != null)
            books.addAll(wrapper.getLibraryBooks());

        return books;
    }

    /**
     * Save LibraryBooks to XML file
     * @param file              saveFile
     * @param books             List of books
     * @throws JAXBException    Marshall exception
     */
    private static void saveBooks(File file, List<LibraryBook> books) throws JAXBException
    {
        JAXBContext context = JAXBContext.newInstance(LibraryBookWrapper.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        // Обёртываем наши данные
        LibraryBookWrapper wrapper = new LibraryBookWrapper();
        wrapper.setLibraryBooks(books);

        // Маршаллируем и сохраняем XML в файл.
        m.marshal(wrapper, file);
    }

    /**
     * Create ZipArchive, that's contained XML file of LibraryBooks
     * @param books             LibraryBooks
     * @return                  Archive, that's contained xmlFile
     * @throws IOException      Read file exceptions
     */
    public static File saveBooksToArchive(List<LibraryBook> books, String path) throws IOException, JAXBException
    {
        File xmlFile = new File(path + "books.xml");
        File archiveFile = new File(path + "books.zip");
        ZipOutputStream zipOutputStream;
        ZipEntry zipEntry;
        Path archivePath;

        // Create xmlFile and archiveFile
        if(!xmlFile.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            xmlFile.createNewFile();
        }
        if(!archiveFile.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            archiveFile.createNewFile();
        }

        // Try to save books to xmlFile
        saveBooks(xmlFile, books);

        // Save xmlFile to ZipArchive
        zipOutputStream = new ZipOutputStream(new FileOutputStream(archiveFile));
        zipEntry = new ZipEntry(xmlFile.getName());
        zipOutputStream.putNextEntry(zipEntry);
        archivePath = Paths.get(xmlFile.getAbsolutePath());
        byte[] data = Files.readAllBytes(archivePath);
        zipOutputStream.write(data, 0, data.length);
        zipOutputStream.closeEntry();
        zipOutputStream.close();

        return archiveFile;
    }

    /**
     * Load Books from archive's xml file
     * @param path              Archive path to load
     * @return                  List of Books
     * @throws IOException      IOException
     * @throws JAXBException    JAXBException
     */
    @Nullable
    public static List<LibraryBook> loadBooksFromArchive(String path) throws IOException, JAXBException
    {
        ZipFile zipFile;
        ZipEntry zipEntry;
        BufferedReader bufferedReader;
        File xmlFile;
        FileOutputStream fileOutputStream;

        File archiveFile = new File(path);
        if(!archiveFile.exists())
            throw new FileNotFoundException(archiveFile.getName() + " not found");

        zipFile = new ZipFile(archiveFile);
        zipEntry = zipFile.getEntry("books.xml");
        if(zipEntry == null)
            return null;
        bufferedReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));

        xmlFile = new File("books.xml");
        if(!xmlFile.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            xmlFile.createNewFile();
        }
        fileOutputStream = new FileOutputStream(xmlFile);

        int in;
        while((in = bufferedReader.read()) != -1)
        {
            fileOutputStream.write(in);
        }
        fileOutputStream.flush();
        fileOutputStream.close();

        return loadBooks(xmlFile);
    }
}
