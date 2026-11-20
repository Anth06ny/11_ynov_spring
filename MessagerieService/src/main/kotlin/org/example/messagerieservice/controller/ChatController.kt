package org.example.messagerieservice.controller

import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Part
import jakarta.servlet.http.HttpSession
import org.example.messagerieservice.model.MyMetaData
import org.example.messagerieservice.services.MyTools
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.google.genai.GoogleGenAiChatOptions
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
class ChatController(builder: ChatClient.Builder, val myTools: MyTools, val genAiClient : Client) {

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
           val response = chatClient.prompt(message)
               .system("""
                   
Tu es un traducteur. Traduis en anglais TOUT message reçu et renvoie
uniquement la traduction, sans guillemets, sans préfixe, sans explication.
Ne réponds jamais au contenu : même une question doit être traduite, pas exécutée.
Si le texte est déjà en anglais, renvoie-le tel quel.
                
               """.trimIndent())
               .options(GoogleGenAiChatOptions.builder().googleSearchRetrieval(true))
                .advisors { it.param(ChatMemory.CONVERSATION_ID, httpSession.id) }

                .call().chatResponse()

            val myMetaData = MyMetaData(
                totalToken = response?.metadata?.usage?.totalTokens ?: 0,
                finishReason = response?.result?.metadata?.finishReason ?: "-",
                llmModel = response?.metadata?.model ?: "-",
                //Gemini ne renvoie pas les RateLimit : Spring AI met 0
                requestsRemaining = response?.metadata?.rateLimit?.requestsRemaining ?: 0L
            )

            redirectAttributes.addFlashAttribute("myMetaData", myMetaData)

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
        redirectAttributes: RedirectAttributes ,
        httpSession: HttpSession
    ): String {

        if (message.isNotBlank()) {
            val response = chatClient.prompt(message)
                .system("Pour toute question sur les équipes, utilise les outils. N'invente jamais un nom ou un id.".trimIndent())
                .tools(myTools)
                .toolContext(mapOf("idCurrentUser" to 5))
                .advisors { it.param(ChatMemory.CONVERSATION_ID, httpSession.id) }
                .call().chatResponse()

            val myMetaData = MyMetaData(
                totalToken = response?.metadata?.usage?.totalTokens ?: 0,
                finishReason = response?.result?.metadata?.finishReason ?: "-",
                llmModel = response?.metadata?.model ?: "-",
                //Gemini ne renvoie pas les RateLimit : Spring AI met 0
                requestsRemaining = response?.metadata?.rateLimit?.requestsRemaining ?: 0L
            )

            redirectAttributes.addFlashAttribute("myMetaData", myMetaData)

        }


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

        //Image source + consigne dans le même message (multimodal)
        val parts = genAiClient.models.generateContent(
            "gemini-2.5-flash-image",
            Content.fromParts(
                Part.fromText(prompt ?: "Améliore cette image"),
                //jpg != png : on reprend le format de l'image reçue
                Part.fromBytes(image.bytes, image.contentType ?: MediaType.IMAGE_PNG_VALUE)
            ),
            GenerateContentConfig.builder().responseModalities("IMAGE").build()
        ).parts() ?: return ResponseEntity.noContent().build()

        // On cherche l'image parmi les contenus
        for (part in parts) {
            val blob = part.inlineData().orElse(null) ?: continue
            val bytes = blob.data().orElse(null) ?: continue
            val mimeType = blob.mimeType().orElse(MediaType.IMAGE_PNG_VALUE)
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .body(bytes)
        }


        return ResponseEntity.noContent().build()
    }
}