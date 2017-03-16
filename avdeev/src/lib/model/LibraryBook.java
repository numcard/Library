package lib.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class LibraryBook
{
    private final Book book;
    private final SimpleLongProperty inventoryNumber;
    private final SimpleBooleanProperty bookFree;

    public long getInventoryNumber()
    {
        return inventoryNumber.get();
    }
    public SimpleLongProperty inventoryNumberProperty()
    {
        return inventoryNumber;
    }
    public void setInventoryNumber(long inventoryNumber)
    {
        this.inventoryNumber.set(inventoryNumber);
    }
    public boolean isBookFree()
    {
        return bookFree.get();
    }
    public SimpleBooleanProperty bookFreeProperty()
    {
        return bookFree;
    }
    public void setBookFree(boolean bookFree)
    {
        this.bookFree.set(bookFree);
    }
    public String getAuthor()
    {
        return book.getAuthor();
    }
    public SimpleStringProperty authorProperty()
    {
        return book.authorProperty();
    }
    public void setAuthor(String author)
    {
        book.setAuthor(author);
    }
    public String getTitle()
    {
        return book.getTitle();
    }
    public SimpleStringProperty titleProperty()
    {
        return book.titleProperty();
    }
    public void setTitle(String title)
    {
        book.setTitle(title);
    }
    public int getCopyright()
    {
        return book.getCopyright();
    }
    public SimpleIntegerProperty copyrightProperty()
    {
        return book.copyrightProperty();
    }
    public void setCopyright(int copyright)
    {
        book.setCopyright(copyright);
    }
    public int getNumberOfPages()
    {
        return book.getNumberOfPages();
    }
    public SimpleIntegerProperty numberOfPagesProperty()
    {
        return book.numberOfPagesProperty();
    }
    public void setNumberOfPages(int numberOfPages)
    {
        book.setNumberOfPages(numberOfPages);
    }

    public LibraryBook()
    {
        book = new Book();
        inventoryNumber = new SimpleLongProperty(0L);
        bookFree = new SimpleBooleanProperty(false);
    }

    private class Book
    {
        private final SimpleStringProperty author;
        private final SimpleStringProperty title;
        private final SimpleIntegerProperty copyright;
        private final SimpleIntegerProperty numberOfPages;

        String getAuthor()
        {
            return author.get();
        }
        SimpleStringProperty authorProperty()
        {
            return author;
        }
        void setAuthor(String author)
        {
            this.author.set(author);
        }
        String getTitle()
        {
            return title.get();
        }
        SimpleStringProperty titleProperty()
        {
            return title;
        }
        void setTitle(String title)
        {
            this.title.set(title);
        }
        int getCopyright()
        {
            return copyright.get();
        }
        SimpleIntegerProperty copyrightProperty()
        {
            return copyright;
        }
        void setCopyright(int copyright)
        {
            this.copyright.set(copyright);
        }
        int getNumberOfPages()
        {
            return numberOfPages.get();
        }
        SimpleIntegerProperty numberOfPagesProperty()
        {
            return numberOfPages;
        }
        void setNumberOfPages(int numberOfPages)
        {
            this.numberOfPages.set(numberOfPages);
        }

        private Book()
        {
            author = new SimpleStringProperty("");
            title = new SimpleStringProperty("");
            copyright = new SimpleIntegerProperty(0);
            numberOfPages = new SimpleIntegerProperty(0);
        }
    }

    @Override
    public String toString()
    {
        return "LibraryBook{" + "book=" + book + ", inventoryNumber=" + inventoryNumber + ", bookFree=" + bookFree + '}';
    }
}
