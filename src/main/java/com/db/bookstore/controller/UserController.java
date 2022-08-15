package com.db.bookstore.controller;

import com.db.bookstore.model.Author;
import com.db.bookstore.model.Book;
import com.db.bookstore.model.BookDTO;
import com.db.bookstore.model.User;

import com.db.bookstore.repository.BookRepository;
import com.db.bookstore.service.AuthorService;
import com.db.bookstore.service.BookService;
import com.db.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    BookService bookService;
    @Autowired
    AuthorService authorService;

    @GetMapping("/register")
    public ModelAndView getRegisterForm(){
        ModelAndView modelAndView = new ModelAndView("register-form");
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView addUser(User user){
        userService.insertUser(user);
        userService.setUserRole(user, "client");
        ModelAndView modelAndView = new ModelAndView("redirect:/login");
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView getLoginForm(){
        ModelAndView modelAndView = new ModelAndView("login-form");
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView verifyUser(User user, HttpServletResponse response){
        try {
            User user1 = userService.findByUsernameOrEmailAndPassword(user);
            response.addCookie(new Cookie("id", "" + user1.getId()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");

        return modelAndView;



    }
    // am verificat cookie-ul din developer tools din Chrome si este setat corect
    @GetMapping("/dashboard")
    public ModelAndView getDashBoard(@CookieValue("id") String userIdCookie){
        int userId = Integer.parseInt(userIdCookie);
        ModelAndView modelAndView = new ModelAndView("dashboard");
        List<Book> listOfBooks = bookService.getBooksList();
        User loggedUser = userService.getUserById(userId);
        modelAndView.addObject("bookList",listOfBooks);
        modelAndView.addObject("user", loggedUser);
        return modelAndView;
    }

    @GetMapping("/add-book")
    public ModelAndView getAddBooks(@CookieValue("id") String userIdCookie) {
        int userId = Integer.parseInt(userIdCookie);
        User loggedUser = userService.getUserById(userId);
        if (!Objects.equals(loggedUser.getRole(), "admin")){
            return new ModelAndView("unauthorized-access");
        } else {
            ModelAndView modelAndView = new ModelAndView("add-book-form");
            Book bookToBeAdded = new Book();
            Set<String> authorsNames = authorService.getAuthorsNames();
            BookDTO bookDTO = new BookDTO();
            modelAndView.addObject("book", bookToBeAdded);
            modelAndView.addObject("authorsNames", authorsNames);
            modelAndView.addObject("bookDTO", bookDTO);
            return  modelAndView;
        }
    }

    @PostMapping("/add-book")
    public ModelAndView addBook(@ModelAttribute BookDTO bookDTO, @ModelAttribute Book bookToAdd) {
        Set<String> authorsNamesSelected = bookDTO.getSelectedAuthorsNames();
        Set<Author> authorsSelected = new HashSet<>();
        ModelAndView modelAndView = new ModelAndView("book-add-message");

        // handle the case where there isn't any author selected
        if (authorsNamesSelected.size() == 0){
            modelAndView.addObject("message", "ERROR! You must select at least 1 author!");
            return modelAndView;
        }

        // insert the author list for the added book
        for(String authorName: authorsNamesSelected){
            Author author = authorService.getAuthorByName(authorName);
            authorsSelected.add(author);
        }
        bookToAdd.setAuthorList(authorsSelected);

//        // insert the book
        bookService.addBook(bookToAdd);

        modelAndView.addObject("message", "Book added successfully!");
        return modelAndView;
    }



}
