package re.dufau.securiteapplicative.securitynightmare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import re.dufau.securiteapplicative.securitynightmare.dao.MessageRepository;
import re.dufau.securiteapplicative.securitynightmare.model.Message;
import re.dufau.securiteapplicative.securitynightmare.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageRepository messageRepository;


    @GetMapping
    public String listUsers(Model model) {
        Map<String,String> users = userService.getUsersToEnable();
        model.addAttribute("users", users);
        return "admin";
    }

    @PostMapping
    public String postMessage(String content, boolean classified,  Principal principal) {
        var newMessage = new Message();
        newMessage.setAuthor(principal.getName());
        newMessage.setContent(content);
        newMessage.setClassified(classified);
        messageRepository.save(newMessage);
        return "redirect:/admin";
    }

    @PostMapping("/promote/{username}")
    public String promoteUser(@PathVariable String username) {
        userService.enableNewUser(username);
        return "redirect:/admin";
    }
}

