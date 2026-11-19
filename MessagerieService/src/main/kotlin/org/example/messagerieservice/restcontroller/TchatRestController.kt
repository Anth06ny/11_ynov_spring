package org.example.messagerieservice.restcontroller

import com.jogamp.common.nio.Buffers.slice
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.example.messagerieservice.model.MessageDTO
import org.example.messagerieservice.services.MessageService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.*

@RequestMapping("/tchat")
@RestController
class TchatRestController(
    val messageService: MessageService,
) {

    //http://localhost:8080/tchat/allMessages
    @GetMapping("/allMessages")
    fun allMessages() = messageService.getALl()

    @Operation(
        summary = "Enregistrer un message",
        description = "Permet d'enregistrer un nouveau message envoyé par un utilisateur"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Message enregistré avec succès"
            )
        ]
    )
    //http://localhost:8080/tchat/saveMessage
    //json : {"pseudo":"toto", "message":"coucou"}
    @PostMapping("/saveMessage")
    fun saveMessage(@Valid @RequestBody message: MessageDTO) {
        messageService.addMessage(message)
    }

    //http://localhost:8080/tchat/addMessage?pseudo=toto&message=coucou
    @GetMapping("/addMessage")
    fun addMessage(pseudo: String, message: String) {
        messageService.addMessage( MessageDTO(null,pseudo, message))
    }

    //http://localhost:8080/tchat/testCron
    @GetMapping("/testCron")
    fun testCron() {
        messageService.keepLast5Message()
    }

    @Scheduled(fixedRate = 60000)
    fun clearTo5() {
        messageService.keepLast5Message()
    }

}