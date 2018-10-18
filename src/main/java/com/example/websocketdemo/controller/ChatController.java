package com.example.websocketdemo.controller;

import com.example.websocketdemo.WebsocketDemoApplication;
import com.example.websocketdemo.model.ChatMessage;
import javax.servlet.http.HttpSession;
import org.alicebot.ab.Chat;
import org.alicebot.ab.History;
import org.alicebot.ab.MagicBooleans;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * Created by rajeevkumarsingh on 24/07/17.
 */
@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {

        Chat chatsession = (Chat) WebsocketDemoApplication.chatSession;

        System.out.println("chat object " + chatsession);
        
        String request = chatMessage.getContent();
        if (MagicBooleans.trace_mode) {
            System.out.println("STATE=" + request + ":THAT=" + ((History) chatsession.thatHistory.get(0)).get(0) + ":TOPIC=" + chatsession.predicates.get("topic"));
        }
        String response = chatsession.multisentenceRespond(request);
        while (response.contains("&lt;")) {
            response = response.replace("&lt;", "<");
        }
        while (response.contains("&gt;")) {
            response = response.replace("&gt;", ">");
        }
        chatMessage.setContent(response);
        chatMessage.setSender("Robot");
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}
