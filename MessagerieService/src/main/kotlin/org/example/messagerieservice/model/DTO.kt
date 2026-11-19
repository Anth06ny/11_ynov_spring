package org.example.messagerieservice.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class StudentDTO(val name:String, val note : Int)
class MessageDTO(
    @field:Size(min = 3, message = "Il faut au moins 3 caractères")
    val pseudo:String,
    @field:NotBlank(message = "Il faut un message")
    val message : String)


data class UserDTO(var id: Long? = null, var login: String = "",
                   var password: String = "")