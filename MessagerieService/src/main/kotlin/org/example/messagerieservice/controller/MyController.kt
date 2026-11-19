package org.example.messagerieservice.controller

import org.example.messagerieservice.model.StudentDTO
import org.example.messagerieservice.model.UserForm
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class MyController {

    //http://localhost:8080/login
    @GetMapping("/login") //Affiche le formulaire
    fun login(userForm: UserForm): String {
        println("/login")
        return "login" //Lance login.html
    }

    @PostMapping("/loginSubmit") //traite le formulaire
    fun loginSubmit(userForm: UserForm, redirectAttributes: RedirectAttributes) : String {
        println("/loginSubmit : userForm=$userForm")


        try {
            if (userForm.login.isBlank()) {
                throw Exception("Login manquant")
            }
            redirectAttributes.addFlashAttribute("userForm", userForm)

            return "redirect:userRegister" // Redirection sur /userRegister
        }
        catch(e:Exception){
            e.printStackTrace()

            redirectAttributes.addFlashAttribute("userForm", userForm)
            redirectAttributes.addFlashAttribute("errorMessage", e.message)
            return "redirect:login" //Redirige sur /login
        }
    }

    @GetMapping("/userRegister") //Affiche la page résultat
    fun userRegister(): String {
        println("/userRegister")
        return "userRegister" //Lance userRegister.html
    }


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