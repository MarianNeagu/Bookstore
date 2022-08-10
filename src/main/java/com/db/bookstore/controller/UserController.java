package com.db.bookstore.controller;

import com.db.bookstore.model.Book;
import com.db.bookstore.model.User;

import com.db.bookstore.repository.BookRepository;
import com.db.bookstore.repository.UserRepository;
import com.db.bookstore.service.BookService;
import com.db.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    BookService bookService;
    BookRepository bookRepository;
    UserRepository userRepository;

    @GetMapping("/register")
    public ModelAndView getRegisterForm(){
        ModelAndView modelAndView = new ModelAndView("register-form");
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView addUser(User user){
        user.setRole("client");
        userService.insertUser(user);
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

    @GetMapping("/dashboard")
    public ModelAndView getDashBoard(@CookieValue(name="id",defaultValue = "default") int id) {
        ModelAndView modelAndView=new ModelAndView("dashboard");
        try {
            User user = userService.findById(id);
            modelAndView.addObject("user", user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            List<Book> listOfBooks = bookService.getAllBooks();
            modelAndView.addObject("bookList", listOfBooks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return modelAndView;
    }

    @GetMapping("/add-book")
    public ModelAndView getAddBook(@CookieValue(name="id",defaultValue = "default") int id) {
        ModelAndView modelAndView=new ModelAndView("add-book");
        try {
            User user = userService.findById(id);
            modelAndView.addObject("user", user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return modelAndView;
    }

    @PostMapping("/add-book")
    public ModelAndView addBook(Book book) {
        bookService.addBook(book);
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        return modelAndView;
    }

}
