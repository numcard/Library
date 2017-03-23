package shared.model;

import javax.xml.bind.annotation.XmlRootElement;

public class LibraryBook
{
    private Book book;
    private long inventoryNumber;
    private boolean bookFree;

    public Book getBook()
    {
        return book;
    }
    @SuppressWarnings("unused")
    public void setBook(Book book)
    {
        this.book = book;
    }
    public long getInventoryNumber()
    {
        return inventoryNumber;
    }
    public void setInventoryNumber(long inventoryNumber)
    {
        this.inventoryNumber = inventoryNumber;
    }
    public boolean isBookFree()
    {
        return bookFree;
    }
    public void setBookFree(boolean bookFree)
    {
        this.bookFree = bookFree;
    }

    public LibraryBook()
    {
        book = new Book();
        inventoryNumber = 0L;
        bookFree = false;
    }

    @XmlRootElement(name = "Book")
    public static class Book
    {
        private String author;
        private String title;
        private int copyright;
        private int numberOfPages;

        public String getAuthor()
        {
            return author;
        }
        public void setAuthor(String author)
        {
            this.author = author;
        }
        public String getTitle()
        {
            return title;
        }
        public void setTitle(String title)
        {
            this.title = title;
        }
        public int getCopyright()
        {
            return copyright;
        }
        public void setCopyright(int copyright)
        {
            this.copyright = copyright;
        }
        public int getNumberOfPages()
        {
            return numberOfPages;
        }
        public void setNumberOfPages(int numberOfPages)
        {
            this.numberOfPages = numberOfPages;
        }

        private Book()
        {
            author = "";
            title = "";
            copyright = 0;
            numberOfPages = 0;
        }
    }
}
