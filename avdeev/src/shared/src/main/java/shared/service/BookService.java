package shared.service;

import shared.model.LibraryBook;
import shared.model.LibraryBookWrapper;

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
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class BookService
{
    private static final String BOOK_DIRECTORY = "data";
    private static final String BOOK_XML = "books.xml";
    private static final String BOOK_XML_PATH = BOOK_DIRECTORY + "/" + "books.xml";
    public static final String BOOK_ARCHIVE_PATH = BOOK_DIRECTORY + "/" + "books.zip";

    /**
     * Load LibraryBooks from XML file and return List of LibraryBooks
     *
     * @param file XML file
     * @return List of LibraryBook
     * @throws FileNotFoundException If file not found
     * @throws JAXBException         If Structure of XML file has wrong format
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
     *
     * @param file  saveFile
     * @param books List of books
     * @throws JAXBException Marshall exception
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
     *
     * @param books LibraryBooks
     * @return Archive, that's contained xmlFile
     * @throws IOException Read file exceptions
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File saveBooksToArchive(List<LibraryBook> books) throws IOException, JAXBException
    {
        File bookXML = new File(BOOK_XML_PATH);
        File bookArchive = new File(BOOK_ARCHIVE_PATH);

        //Check and create files
        createFiles();

        // Try to save books to xmlFile
        saveBooks(bookXML, books);

        // Save xmlFile to ZipArchive
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(bookArchive));
        ZipEntry zipEntry = new ZipEntry(bookXML.getName());
        zipOutputStream.putNextEntry(zipEntry);
        Path archivePath = Paths.get(bookXML.getAbsolutePath());
        byte[] data = Files.readAllBytes(archivePath);
        zipOutputStream.write(data, 0, data.length);
        zipOutputStream.closeEntry();
        zipOutputStream.close();

        return bookArchive;
    }

    /**
     * Load Books from archive's xml file
     *
     * @return List of Books
     * @throws IOException   IOException
     * @throws JAXBException JAXBException
     */
    public static List<LibraryBook> loadBooksFromArchive() throws IOException, JAXBException
    {
        File bookArchive = new File(BOOK_ARCHIVE_PATH);
        if(!bookArchive.exists())
            throw new FileNotFoundException(bookArchive.getAbsolutePath());

        File bookXML;
        try(ZipFile zipFile = new ZipFile(bookArchive))
        {
            if(zipFile.size() == 0)
                return new ArrayList<>();

            ZipEntry zipEntry = zipFile.getEntry(BOOK_XML);
            if(zipEntry == null)
                return new ArrayList<>();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));

            bookXML = new File(BOOK_XML_PATH);
            if(!bookXML.exists())
            {
                //noinspection ResultOfMethodCallIgnored
                bookXML.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(bookXML);

            int in;
            while((in = bufferedReader.read()) != -1)
            {
                fileOutputStream.write(in);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            zipFile.close();
        }
        catch(ZipException e)
        {
            return new ArrayList<>();
        }

        return loadBooks(bookXML);
    }

    /**
     * Check temp files's existed, if necessary files will be created
     * @throws IOException IOException
     */
    public static void createFiles() throws IOException
    {
        File bookDir = new File(BOOK_DIRECTORY);
        File bookXML = new File(BOOK_XML_PATH);
        File bookArchive = new File(BOOK_ARCHIVE_PATH);
        boolean status = true;
        // Create files and directory
        if(!bookDir.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            bookDir.mkdir();
        }

        if(!bookXML.exists())
            status = bookXML.createNewFile();
        if(!status)
            throw new FileNotFoundException(bookXML.getAbsolutePath());

        if(!bookArchive.exists())
            status = bookArchive.createNewFile();
        if(!status)
            throw new FileNotFoundException(bookArchive.getAbsolutePath());
    }
}
