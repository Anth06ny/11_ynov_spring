package org.example.messagerieservice.services

import org.springframework.ai.chat.model.ToolContext
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Component


@Component
class MyTools {

    @Tool(description = "Donne les prénoms des personnes actuellement connectées")
    fun getConnectedPeople(): List<String> {
        println("getConnectedPeople() call")
        return listOf(
            "Bertrand", "Roget", "Julie", "Chantal", "Gwen",
            "Marc", "Sophie", "Nadia", "Hugo", "Léa",
        )
    }

    @Tool(description = "Donne les prénoms des personnes qui travaillent sur un projet")
    fun getPeopleOnProject(@ToolParam(description = "Id du projet (voir getProject)") idProject: Int): List<String> {
        println("getPeopleOnProject($idProject) call")
        return mapOf(
            1 to listOf("Bertrand", "Roget", "Julie"),
            2 to listOf("Chantal", "Gwen", "Marc", "Sophie"),
            3 to listOf("Bertrand", "Nadia", "Hugo"),
            4 to listOf("Julie", "Léa", "Thomas", "Karim"),
            5 to listOf("Gwen", "Émilie", "Paul"),
            6 to listOf("Roget", "Sophie", "Victor", "Nadia"),
            7 to listOf("Chantal", "Hugo", "Camille"),
            8 to listOf("Marc", "Léa", "Antoine", "Fatima"),
            9 to listOf("Paul", "Camille", "Yann"),
            10 to listOf("Thomas", "Émilie", "Karim", "Olivier"),
        )[idProject] ?: emptyList()
    }

    @Tool(description = "Donne la liste des projets avec leur nom et leur id")
    fun getProject(): List<Project> {
        println("getProject() call")
        return listOf(
            Project("Atlas", 1),
            Project("Borealis", 2),
            Project("Cyclone", 3),
            Project("Delta", 4),
            Project("Eclipse", 5),
            Project("Falcon", 6),
            Project("Galaxy", 7),
            Project("Horizon", 8),
            Project("Iris", 9),
            Project("Jupiter", 10),
        )
    }

    @Tool(description = "Donne le salaire de l'utilisateur connecté")
    fun getSalary(toolContext: ToolContext): Int {          // ← pas de idUser en paramètre LLM !
        val userId = (toolContext.context["idCurrentUser"] as? Int) ?: -1
        return getSalary(userId) // l'id vient du contexte, pas du modèle
    }


    //@Tool(description = "Donne le salaire mensuel brut en euros de l'idUser")
    fun getSalary(idUser: Int): Int {
        println("getSalary($idUser) call")
        return mapOf(
            1 to 2450,
            2 to 3100,
            3 to 2800,
            4 to 3500,
            5 to 2950,
            6 to 4200,
            7 to 2600,
            8 to 3800,
            9 to 3300,
            10 to 5000,
        )[idUser] ?: 0
    }
}

data class Project(val name: String, val id: Int)