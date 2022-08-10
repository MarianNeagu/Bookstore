package com.db.bookstore.service;

import com.db.bookstore.model.Book;
import com.db.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {
    @Autowired
    BookRepository bookRepository;

    public void addBook(Book book){
        bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

}
