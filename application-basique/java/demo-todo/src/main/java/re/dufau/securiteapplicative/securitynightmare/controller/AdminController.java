package re.dufau.securiteapplicative.securitynightmare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import re.dufau.securiteapplicative.securitynightmare.dao.MessageRepository;
import re.dufau.securiteapplicative.securitynightmare.model.Message;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping
    public String listUsers(Model model) {
        return "admin";
    }

    @PostMapping
    public String postMessage(String content, boolean classified, Principal principal) {
        var newMessage = new Message();
        newMessage.setAuthor(principal.getName());
        newMessage.setContent(content);
        newMessage.setClassified(classified);
        messageRepository.save(newMessage);
        return "redirect:/admin";
    }

}

