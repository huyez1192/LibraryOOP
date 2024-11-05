package Main;

//import java.sql.*;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        BookDAO bookDAO = new BookDAO();

        Book[] books = {
            createBook("3RhKDwAAQBAJ", "Java Programming for Beginners", "Mark Lassoff", "Java Programming for Beginners is an introduction...", "Computers", "http://books.google.com/books/content?id=3RhKDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api"),
            createBook("1DvJtwEACAAJ", "Effective Java", "Joshua Bloch", "A definitive guide for Java programmers...", "Computers", "http://books.google.com/books/content?id=1DvJtwEACAAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api"),
            createBook("6VhKDwAAQBAJ", "Head First Java", "Kathy Sierra, Bert Bates", "An interactive book on Java programming...", "Computers", "http://books.google.com/books/content?id=6VhKDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api"),
            createBook("2RhKDwAAQBAJ", "Java: The Complete Reference", "Herbert Schildt", "Comprehensive guide to Java programming...", "Computers", "http://books.google.com/books/content?id=2RhKDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api"),
            createBook("4RhKDwAAQBAJ", "Core Java Volume I–Fundamentals", "Cay S. Horstmann", "Essential guide for Java beginners...", "Computers", "http://books.google.com/books/content?id=4RhKDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api"),
            createBook("5RhKDwAAQBAJ", "Java in a Nutshell", "Benjamin J. Evans, David Flanagan", "Quick reference guide for Java...", "Computers", "http://books.google.com/books/content?id=5RhKDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api"),
            createBook("6RhKDwAAQBAJ", "Java Performance: The Definitive Guide", "Scott Oaks", "Guide to improve Java performance...", "Computers", "http://books.google.com/books/content?id=6RhKDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api"),
            createBook("7RhKDwAAQBAJ", "Java Concurrency in Practice", "Brian Goetz", "Comprehensive guide to concurrency in Java...", "Computers", "http://books.google.com/books/content?id=7RhKDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api"),
            createBook("8RhKDwAAQBAJ", "Thinking in Java", "Bruce Eckel", "A deep dive into Java programming concepts...", "Computers", "http://books.google.com/books/content?id=8RhKDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api"),
            createBook("9RhKDwAAQBAJ", "Pro Java Programming", "Brett Spell", "A professional’s guide to Java...", "Computers", "http://books.google.com/books/content?id=9RhKDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api"),
            createBook("10RhKDwAAQBAJ", "Java Network Programming", "Elliotte Rusty Harold", "Comprehensive guide to Java networking...", "Computers", "http://books.google.com/books/content?id=10RhKDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api"),
            createBook("11RhKDwAAQBAJ", "Modern Java Recipes", "Ken Kousen", "Practical solutions for common Java problems...", "Computers", "http://books.google.com/books/content?id=11RhKDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api")
        };

        for (Book book : books) {
            bookDAO.addBook(book);
        }
    }

    public static Book createBook(String id, String title, String authors, String description, String categories, String thumbnailLink) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthors(authors);
        book.setDescription(description);
        book.setCategories(categories);
        book.setThumbnailLink(thumbnailLink);
        return book;
    }
}
