package com.db.bookstore.repository;

import com.db.bookstore.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    @Query(value="SELECT name FROM Author")
    Set<String> getAuthorsNames();

    Author findAuthorByName(String name);
}
