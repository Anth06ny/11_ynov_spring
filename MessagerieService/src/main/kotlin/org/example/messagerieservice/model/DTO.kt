package org.example.messagerieservice.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.proxy.HibernateProxy

class StudentDTO(val name:String, val note : Int)

@Entity
@Table(name = "message")
 class MessageDTO(
    @Id ///ID auto incrémenté
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,
    @field:Size(min = 3, message = "Il faut au moins 3 caractères")
    var pseudo:String = "",
    @field:NotBlank(message = "Il faut un message")
    var message : String = "")    {



}


data class UserDTO(var id: Long? = null, var login: String = "",
                   var password: String = "")


data class MyMetaData(
    val totalToken: Int,
    val finishReason: String,
    val llmModel: String,
    val requestsRemaining: Long
)