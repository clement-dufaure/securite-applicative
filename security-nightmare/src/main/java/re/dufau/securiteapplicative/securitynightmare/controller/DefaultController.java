package re.dufau.securiteapplicative.securitynightmare.controller;


import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import re.dufau.securiteapplicative.securitynightmare.dao.MessageRepository;
import re.dufau.securiteapplicative.securitynightmare.model.Message;
import re.dufau.securiteapplicative.securitynightmare.service.UserService;

import java.net.http.HttpRequest;
import java.security.Principal;
import java.util.List;

@Controller
public class DefaultController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String getWelcomePage(Model model) {
        return "welcome";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("message") String message) {
        userService.saveNewUser(username, password, message);
        return "redirect:/";
    }

}
