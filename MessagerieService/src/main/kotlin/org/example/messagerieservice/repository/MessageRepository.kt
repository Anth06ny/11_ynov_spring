package org.example.messagerieservice.repository

import jakarta.transaction.Transactional
import org.example.messagerieservice.model.MessageDTO
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository : JpaRepository<MessageDTO, Long> {


    fun findFirst10ByOrderByIdDesc() : List<MessageDTO>

    fun findTop5ByOrderByIdDesc(): List<MessageDTO>

    @Transactional
    fun deleteByIdLessThan(id: Long)   // à appeler dans une méthode @Transactional
}