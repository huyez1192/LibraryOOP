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
