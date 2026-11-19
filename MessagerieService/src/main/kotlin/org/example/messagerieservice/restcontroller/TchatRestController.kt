package org.example.messagerieservice.restcontroller

import com.jogamp.common.nio.Buffers.slice
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.example.messagerieservice.model.MessageDTO
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.*

@RequestMapping("/tchat")
@RestController
class TchatRestController {

    var list = listOf<MessageDTO>()


    //http://localhost:8080/tchat/allMessages
    @GetMapping("/allMessages")
    fun allMessages() = list

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
        list = list + message
    }

    //http://localhost:8080/tchat/addMessage?pseudo=toto&message=coucou
    @GetMapping("/addMessage")
    fun addMessage(pseudo: String, message: String) {
        list = list + MessageDTO(pseudo, message)
    }

    @Scheduled(fixedRate = 60000)
    fun clearTo5() {
        if(list.size > 5 ) {
            list = list.reversed().slice(0..5)
        }
    }

}