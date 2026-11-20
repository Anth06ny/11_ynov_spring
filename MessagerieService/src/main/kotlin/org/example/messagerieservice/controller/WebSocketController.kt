package org.example.messagerieservice.controller

import org.example.messagerieservice.config.CHANNEL_NAME
import org.example.messagerieservice.model.MessageDTO
import org.example.messagerieservice.services.MessageService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import java.util.concurrent.CopyOnWriteArrayList

@Controller
class WebSocketController(val messageService: MessageService) {

    //Thread-safe : plusieurs messages peuvent arriver en même temps
    //private val messageHistory = CopyOnWriteArrayList<MessageDTO>()

    @MessageMapping("/chat") // Le client envoie sur /app/chat
    @SendTo(CHANNEL_NAME) // Le retour est publié sur /topic/messages
    fun receiveMessage(message: MessageDTO): MessageDTO {
        println("/app/chat $message")
        messageService.addMessage(message)
        return message // Seul le nouveau message est envoyé
    }

    @SubscribeMapping("/history") // Le client s'abonne à /app/history
    fun getHistory(): List<MessageDTO> = messageService.getALl() // Envoyé uniquement au client qui s'abonne

    //Pour publier depuis n'importe quelle classe (ex : un RestController)
    //class MonRestController(val messagingTemplate: SimpMessagingTemplate)
    //...
    //messagingTemplate.convertAndSend(CHANNEL_NAME, message)
}