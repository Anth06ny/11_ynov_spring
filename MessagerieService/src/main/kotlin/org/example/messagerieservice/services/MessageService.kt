package org.example.messagerieservice.services

import org.example.messagerieservice.model.MessageDTO
import org.example.messagerieservice.repository.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val messageRepository : MessageRepository
) {

    fun addMessage(message: MessageDTO) : MessageDTO {
        messageRepository.save(message)

        return message
    }

    fun getALl() = messageRepository.findAll()


    fun keepLast10Message(){
        val id = messageRepository.findTop10ByOrderByIdDesc().last().id ?: 0
        messageRepository.deleteByIdLessThan(id)
    }
}