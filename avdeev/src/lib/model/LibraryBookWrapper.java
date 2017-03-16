package lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "libraryBook")
public class LibraryBookWrapper
{
    private List<LibraryBook> libraryBooks;

    public void setLibraryBooks(List<LibraryBook> libraryBooks)
    {
        this.libraryBooks = libraryBooks;
    }

    @XmlElement(name = "libraryBook")
    public List<LibraryBook> getLibraryBooks()
    {
        return libraryBooks;
    }
}
