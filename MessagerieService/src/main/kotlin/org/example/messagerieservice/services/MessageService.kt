package org.example.messagerieservice.services

import org.example.messagerieservice.model.MessageDTO
import org.example.messagerieservice.repository.MessageRepository
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val messageRepository : MessageRepository
) {

    fun get10Last(): List<MessageDTO> {
        return messageRepository.findFirst10ByOrderByIdDesc()
    }

    fun findById(id: Long): MessageDTO? {
        return messageRepository.findById(id).orElse(null)
    }

    fun addMessage(message: MessageDTO) : MessageDTO {
        messageRepository.save(message)

        return message
    }

    fun getALl() = messageRepository.findAll()


    fun keepLast5Message(){
        val id = messageRepository.findTop5ByOrderByIdDesc().lastOrNull()?.id ?: 0
        messageRepository.deleteByIdLessThan(id)
    }
}