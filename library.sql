DROP DATABASE IF EXISTS library;
CREATE DATABASE library;
USE library;

-- Users table
CREATE TABLE Users (
   user_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
   full_name VARCHAR(45) NOT NULL,
   user_name VARCHAR(45) NOT NULL UNIQUE,
   pass_word VARCHAR(255) NOT NULL,  -- Mật khẩu nên được mã hóa (hash) trước khi lưu
   email VARCHAR(100) NOT NULL UNIQUE,
   PRIMARY KEY (user_id)
);

--books table
CREATE TABLE Books (
     isbn VARCHAR(13) PRIMARY KEY,
     title VARCHAR(255) NOT NULL,
     description TEXT,
     thumbnail_link VARCHAR(255),
     previewLink VARCHAR(255),
     authors TEXT NOT NULL,
     description TEXT,
     categories TEXT,
     publisher text,
     quantity int not null
);

-- BorrowedBooks table
CREATE TABLE BorrowedBooks (
     borrow_id INT PRIMARY KEY AUTO_INCREMENT,
     user_id INT UNSIGNED NOT NULL,
     isbn  VARCHAR(13) NOT NULL,
     borrow_date DATE,
     return_date DATE,
     status ENUM('borrowed', 'returned', 'overdue') DEFAULT 'borrowed',
     FOREIGN KEY (user_id) REFERENCES Users(user_id),
     FOREIGN KEY (isbn) REFERENCES Books(isbn)
);

-- Fines table
CREATE TABLE Fines (
	fine_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNSIGNED NOT NULL,
    borrow_id INT NOT NULL,
    fine_amount DOUBLE NOT NULL,
    due_date DATE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (borrow_id) REFERENCES BorrowedBooks(borrow_id)
);

-- Requests table
CREATE TABLE Requests (
	request_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNSIGNED NOT NULL,
    isbn VARCHAR(13) NOT NULL,
    request_date DATE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (isbn) REFERENCES Books(isbn)
);

UPDATE Users u
SET u.userfavorite = (
    SELECT most_requested_category.category
    FROM (
        SELECT r.user_id, b.categories AS category, COUNT(*) AS request_count
        FROM Requests r
        JOIN Books b ON r.isbn = b.isbn
        GROUP BY r.user_id, b.categories
        ORDER BY request_count DESC
        LIMIT 1
    ) AS most_requested_category
    WHERE most_requested_category.user_id = u.user_id
);

ALTER TABLE Users ADD COLUMN userfavorite VARCHAR(100);