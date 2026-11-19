package org.example.messagerieservice

import org.example.messagerieservice.model.MessageDTO
import org.example.messagerieservice.repository.MessageRepository
import org.example.messagerieservice.services.MessageService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class MessageServiceWithMockTest() {

    @Mock
    lateinit var messageRepository: MessageRepository


    @InjectMocks
    lateinit var messageService: MessageService


    @Test
    fun testAddMessage() {
        val messageEntity = MessageDTO(pseudo = "Alice", message = "Hello World")

        // Exécution de la méthode à tester
        messageService.addMessage(messageEntity)

        // Vérification que messageRepository.save() a été appelé avec le bon argument
        Mockito.verify(messageRepository, times(1)).save(messageEntity)
    }

    @Test
    fun testGet10Last() {

        val messages10 = ArrayList<MessageDTO>()
        // Préparation des données
        for (i in 1..15) {
            val message = MessageDTO(pseudo = "User$i", message = "Message $i")
            messageService.addMessage(message)
            messages10.add(message)
        }

        Mockito.`when`(messageRepository.findFirst10ByOrderByIdDesc()).thenReturn(
            messages10.takeLast(10)
        )

        // Exécution de la méthode à tester
        val messages = messageService.get10Last()

        // Vérifications
        Assertions.assertEquals(10, messages.size)
        Assertions.assertEquals("Message 15", messages.last().message)
    }

}