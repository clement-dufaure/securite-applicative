package re.dufau.securiteapplicative.securitynightmare.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
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

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping
    public String getForumPage(Model model, HttpServletRequest request, Principal principal) {
        List<Message> messages = messageRepository.findAll();
        if (!request.isUserInRole("ADMIN")) {
            messages.stream().filter(Message::isClassified).forEach(m -> m.setContent("***classified***"));
        }

        // Récupération des informations standards
        model.addAttribute("messages", messages);
        if (principal == null) {
            model.addAttribute("username", "PAS AUTHENTIFIE !");
        } else {
            if (request.isUserInRole("ADMIN")) {
                model.addAttribute("username", principal.getName() + " (est un admin)");
            } else {
                model.addAttribute("username", principal.getName());
            }
        }

        // Obtenir le jeton pour le retransmettre à un autre service
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        "myauthserver", // client registrationId
                        authentication.getName());
        var accessToken = client.getAccessToken();
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
