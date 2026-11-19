package org.example.messagerieservice.controller

import org.example.messagerieservice.model.StudentDTO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MyController {

    //http://localhost:8080/hello
    @GetMapping("/hello")
    fun testHello(model: Model): String {
        println("/hello")

        var student = StudentDTO("Toto", 15)

        model.addAttribute("message", "Bonjour ")
        model.addAttribute("student", student)
        model.addAttribute("list", listOf(
            StudentDTO("Bob", 10),
            StudentDTO("Bobby", 18),
            StudentDTO("Charles", 20),
        ))

        //Nom du fichier HTML que l'on souhaite afficher
        return "welcome"
    }

}