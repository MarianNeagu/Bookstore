package com.db.bookstore.service;

import com.db.bookstore.model.Author;
import com.db.bookstore.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthorService {
    @Autowired
    AuthorRepository authorRepository;

    public Set<String> getAuthorsNames() {
        return authorRepository.getAuthorsNames();
    }

    public Author getAuthorByName(String name){
        return authorRepository.findAuthorByName(name);
    }
}
