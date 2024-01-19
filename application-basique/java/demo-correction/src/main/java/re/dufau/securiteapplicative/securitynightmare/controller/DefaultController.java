package re.dufau.securiteapplicative.securitynightmare.controller;


import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import re.dufau.securiteapplicative.securitynightmare.dao.MessageRepository;
import re.dufau.securiteapplicative.securitynightmare.model.Message;

import java.security.Principal;
import java.util.List;

@Controller
public class DefaultController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/")
    public String getWelcomePage(Model model) {
        return "welcome";
    }

}
