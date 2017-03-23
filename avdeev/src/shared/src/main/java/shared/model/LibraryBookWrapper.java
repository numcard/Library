package shared.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "libraryBooks")
public class LibraryBookWrapper
{
    private List<LibraryBook> libraryBooks;

    public void setLibraryBooks(List<LibraryBook> libraryBooks)
    {
        this.libraryBooks = libraryBooks;
    }

    @XmlElement(name = "LibraryBook")
    public List<LibraryBook> getLibraryBooks()
    {
        return libraryBooks;
    }
}
