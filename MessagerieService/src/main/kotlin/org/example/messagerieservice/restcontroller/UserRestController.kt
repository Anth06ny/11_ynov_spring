package org.example.messagerieservice.restcontroller

import org.example.messagerieservice.model.UserDTO
import org.example.messagerieservice.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserRestController {

    //http://localhost:8080/users
    @GetMapping
    fun getAllUsers() =  ResponseEntity.ok(UserService.load())


    //http://localhost:8080/users/1
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserDTO> {
        val userDTO = UserService.findById(id)
        return if (userDTO != null) {
            ResponseEntity.ok(userDTO)
        }
        else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    //http://localhost:8080/users
    //{"login":"aaa", "password":"bbb"}
    @PostMapping
    fun createUser(@RequestBody user: UserDTO) = ResponseEntity(UserService.save(user), HttpStatus.CREATED)

    //http://localhost:8080/users/1
    //{"login":"aaa", "password":"bbb"}
    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody userDetails: UserDTO
    ): ResponseEntity<UserDTO> {
        val user = UserService.findById(id)
        return if (user != null) {
            userDetails.id = id //écrase celui reçu dans le JSON au cas ou
            UserService.save(userDetails)
            ResponseEntity(userDetails, HttpStatus.OK)
        }
        else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    //http://localhost:8080/users/1
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        return if (UserService.deleteById(id)) {
            ResponseEntity(HttpStatus.NO_CONTENT)
        }
        else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

}