package org.example.messagerieservice

import org.example.messagerieservice.model.MessageDTO
import org.example.messagerieservice.services.MessageService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
class MessageServiceTest(@Autowired val messageService: MessageService) {

    @Test
    fun testAddMessage() {
        val messageEntity = MessageDTO(pseudo = "Alice", message = "Hello World")

        // Exécution de la méthode à tester
        messageService.addMessage(messageEntity)

        assertNotNull(messageEntity.id, "Id non ajouté");
        assertTrue(messageEntity.id!! > 0, "L'id n'a pas été modifié");

        // Vérification que messageRepository.save() a été appelé avec le bon argument
        val inDatabase = messageService.findById(messageEntity.id!!)

        assertNotNull(inDatabase, "Message non retrouvé en base");
        assertEquals(messageEntity.message, inDatabase.message, "Les attributs sont différents");
        assertEquals(messageEntity.pseudo, inDatabase.pseudo, "Les attributs sont différents");
    }

    @Test
    fun testGet10Last() {
        // Préparation des données
        for (i in 1..15) {
            val message = MessageDTO(pseudo = "User$i", message = "Message $i")
            messageService.addMessage(message)
        }

        // Exécution de la méthode à tester
        val messages = messageService.get10Last()

        // Vérifications
        Assertions.assertEquals(10, messages.size)
        Assertions.assertEquals("Message 15", messages[0].message)
    }

}