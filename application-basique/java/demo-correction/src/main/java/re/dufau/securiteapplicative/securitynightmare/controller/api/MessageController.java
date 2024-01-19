package re.dufau.securiteapplicative.securitynightmare.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import re.dufau.securiteapplicative.securitynightmare.dao.MessageRepository;
import re.dufau.securiteapplicative.securitynightmare.model.Message;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/{username}")
    @ResponseBody
    public List<Message> getMessagesForOneUser(@PathVariable String username) throws Exception {
        List<Message> messages = messageRepository.findAll();
        return messages;
    }


}
