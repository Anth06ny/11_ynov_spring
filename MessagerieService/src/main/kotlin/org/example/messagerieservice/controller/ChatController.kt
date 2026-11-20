package org.example.messagerieservice.controller

import jakarta.servlet.http.HttpSession
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes


data class ChatMessage(val role: String, val content: String)

@Controller
class ChatController(builder: ChatClient.Builder) {

    //OÙ : en mémoire vive, QUOI : les 2 derniers messages
    private val chatMemory: ChatMemory = MessageWindowChatMemory.builder()
        .chatMemoryRepository(InMemoryChatMemoryRepository())
        .maxMessages(2)
        .build()

    private val chatClient by lazy { builder
        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        .build() }


    //http://localhost:8080/chat
    @GetMapping("/chat")
    fun chat(model: Model, httpSession: HttpSession): String {
        val messages = chatMemory.get(httpSession.id).map { ChatMessage(it.messageType.value, it.text ?: "") }
        model.addAttribute("messages", messages)
        return "chat"
    }

    @PostMapping("/chat")
    fun sendMessage(@RequestParam("message") message: String, redirectAttributes: RedirectAttributes, httpSession: HttpSession): String {
        if (message.isNotBlank()) {
            chatClient.prompt(message)
                .advisors { it.param(ChatMemory.CONVERSATION_ID, httpSession.id) }
                .call().content()
        }
        return "redirect:chat"
    }

    @PostMapping("/chat/clear")
    fun clear(httpSession: HttpSession): String {
        chatMemory.clear(httpSession.id)
        return "redirect:/chat"
    }

    //TP TOOLING
    @PostMapping("/chatWithTooling")
    fun sendMessageWithTooling(
        @RequestParam("message") message: String,
        redirectAttributes: RedirectAttributes
    ): String {

        //TODO TP TOOLING

        return "redirect:chat"
    }

    // TP RAG ajout de document
    @PostMapping("/chat/rag/ingest")
    @ResponseBody
    fun ingestDocument(@RequestParam("document") document: MultipartFile): ResponseEntity<String> {
        if (document.isEmpty) {
            return ResponseEntity.badRequest().body("Fichier vide.")
        }

        //TODO TP RAG Ajouter le document au store
        val chunks = 0

        return ResponseEntity.ok(
            "✅ « ${document.originalFilename} » indexé : $chunks morceau(x) ajouté(s) au VectorStore."
        )
    }


    // TP RAG Envoi de message
    @PostMapping("/chatWithRag")
    fun sendMessageWithRag(
        @RequestParam("message") message: String,
        httpSession: HttpSession,
        redirectAttributes: RedirectAttributes
    ): String {

        // TODO TP RAG Envoi de message

        return "redirect:chat"
    }

    // TP Image
    @PostMapping("/chat/transform")
    @ResponseBody
    fun transformImage(
        @RequestParam("image") image: MultipartFile,
        @RequestParam("message", required = false) prompt: String?
    ): ResponseEntity<ByteArray> {

        val transformed: ByteArray = image.bytes
        println(prompt)

        //Todo TP Image

        val contentType = image.contentType ?: MediaType.IMAGE_PNG_VALUE
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(transformed)
    }
}