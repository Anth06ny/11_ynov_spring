package org.example.messagerieservice.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

//Channel que les clients viendront écouter et dans lequel on publiera
//Il peut y avoir plusieurs channels, tous préfixés par /topic
const val CHANNEL_NAME: String = "/topic/messages"

@Configuration //Création d'un fichier de configuration pour les WebSocket
@EnableWebSocketMessageBroker
open class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    // Le point de terminaison pour les connexions WebSocket (handshake)
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws/chat-app")
        //Si le client est sur un autre domaine : .setAllowedOriginPatterns("*")
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        //Préfixe des channels gérés par le broker, que les clients écoutent
        //Convention : /topic (diffusion à tous), /queue (message individuel)
        registry.enableSimpleBroker("/topic")
        //Préfixe des messages envoyés par les clients vers les @MessageMapping
        //Doit être différent de celui du broker
        registry.setApplicationDestinationPrefixes("/app")
    }
}