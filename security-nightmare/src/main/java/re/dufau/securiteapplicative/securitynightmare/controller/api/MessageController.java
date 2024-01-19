package re.dufau.securiteapplicative.securitynightmare.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import re.dufau.securiteapplicative.securitynightmare.model.Message;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Controller
//@CrossOrigin(origins = "*")
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    DataSource datasource;

    @GetMapping("/{username}")
    @ResponseBody
    public List<Message> getMessagesForOneUser(@PathVariable String username) throws Exception{
        List<Message> messages = new ArrayList<>();
        var result = datasource.getConnection().createStatement().executeQuery("SELECT * FROM message where author='" + username +"' and not classified");
        while(result.next()){
            String content = result.getString("content");
            Message m  = new Message();
            m.setAuthor(username);
            m.setContent(content);
            messages.add(m);
        }
        return messages;
    }



}
