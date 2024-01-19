package re.dufau.securiteapplicative.securitynightmare.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/forum")
public class ForumController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping
    public String getForumPage(Model model, HttpServletRequest request, Principal principal) {
        List<Message> messages = messageRepository.findAll();
        if (!request.isUserInRole("ADMIN")) {
            messages.stream().filter(Message::isClassified).forEach(m -> m.setContent("***classified***"));
        }
        model.addAttribute("messages", messages);
        if (principal == null) {
            model.addAttribute("username", "PAS AUTHENTIFIE !");
        } else {
            model.addAttribute("username", request.getRemoteUser());
        }
        return "forum";
    }

    @PostMapping
    public String postMessage(String content, Principal principal) {
        var newMessage = new Message();
        newMessage.setAuthor(principal.getName());
        newMessage.setContent(content);
        messageRepository.save(newMessage);
        return "redirect:/forum";
    }


}
