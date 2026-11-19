package org.example.messagerieservice.restcontroller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import java.time.LocalDateTime


//Quel format d'erreur on souhaite
data class ErrorDTO(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int = HttpStatus.INTERNAL_SERVER_ERROR.value(),
    val error: String = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
    val message: String = "Une erreur interne est survenue",
    val path: String,
    val details: List<String>? = null
)

//Centraliser les erreurs d'une API Rest
@RestControllerAdvice
class GlobalExceptionHandler {

    //On transforme toutes les exceptions en ErrorDTO retourné sous forme JSON
    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorDTO> {

        if (ex is HttpRequestMethodNotSupportedException ||
            ex is NoHandlerFoundException ||
            ex is HttpMediaTypeNotSupportedException
        ) {
            // Laisser Spring gérer ces erreurs
            throw ex
        }

        val error = ErrorDTO(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "Une erreur interne est survenue",
            path = request.requestURI,
            details = listOfNotNull(ex.message)
        )

        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    //On peut appliquer un traitement spécifique pour certaine exception, ici celle du validator
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException,
                               request: HttpServletRequest): ResponseEntity<ErrorDTO> {
        val errors = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        val error = ErrorDTO(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation failed",
            message = "Invalid request body",
            path = request.requestURI,
            details = errors
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

}