package org.example.messagerieservice.restcontroller

import org.example.messagerieservice.model.StudentDTO
import org.springframework.ai.chat.client.ChatClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MyRestController(builder : ChatClient.Builder) {

    //Injection de dépendance du Builder de ChatClient
    val chatClient by lazy { builder.build() }

    //http://localhost:8080/testAI
    @GetMapping("/testAI")
    fun testAI(): String {
        println("/testAI")

        //First prompt
        val response = chatClient.prompt("Tell me a joke").call().content()

        return response ?: "Réponse null"
    }

    //http://localhost:8080/testPublic
    @GetMapping("/testPublic")
    fun testPublic(): String {
        println("/testPublic")
        return "Hello public"
    }

    //http://localhost:8080/testPrivate
    @GetMapping("/testPrivate")
    fun testPrivate(): String {
        println("/testPrivate")
        return "Hello private"
    }

    //http://localhost:8080/testPrivateAdmin
    @GetMapping("/testPrivateAdmin")
    fun testPrivateAdmin(): String {
        println("/testPrivateAdmin")
        return "Hello private admin"
    }

    // http://localhost:8080/max?p1=5&p2=6
    @GetMapping("/max")
    fun getMax(p1: String? = "", p2: String? = ""): Int? {

        val p1Int = p1?.toIntOrNull()
        val p2Int = p2?.toIntOrNull()

        if(p1Int == null) return p2Int
        if(p2Int == null) return p1Int
        return Math.max(p1Int , p2Int)
    }

    //http://localhost:8080/createStudent?name=bob&notation=12
    @GetMapping("/createStudent")
    fun createStudent(
        name: String = "",
        @RequestParam(value = "notation", defaultValue = "0") note: Int
    ): StudentDTO {
        //name contiendra bob
        //note contiendra 12
        println("/createStudent : name=$name note=$note")

        return StudentDTO(name, note)
    }

    //http://localhost:8080/receiveStudent
//Json Attendu : {"name": "toto","note": 12}
    @PostMapping("/receiveStudent")
    fun receiveStudent(@RequestBody student: StudentDTO) {
        println("/receiveStudent : $student")

        //traitement, mettre en base…
        //Retourner d'autres données
    }

}