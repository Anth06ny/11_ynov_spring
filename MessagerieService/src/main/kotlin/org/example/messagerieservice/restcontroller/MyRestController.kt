package org.example.messagerieservice.restcontroller

import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.ImageConfig
import com.google.genai.types.Part
import com.google.genai.types.PrebuiltVoiceConfig
import com.google.genai.types.SpeechConfig
import com.google.genai.types.VoiceConfig
import org.example.messagerieservice.model.StudentDTO
import org.springframework.ai.chat.client.ChatClient
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

@RestController
class MyRestController(builder : ChatClient.Builder, val genAiClient: Client) {

    //Injection de dépendance du Builder de ChatClient
    val chatClient by lazy { builder.build() }


    //http://localhost:8080/tts
    /**
     * TTS = TextToSpeech
     * Le LLM retourne une suite de nombres représentant le son
     */
    @GetMapping("/tts")
    fun tts(message: String? = null): ResponseEntity<ByteArray> {
        val prompt = if (!message.isNullOrBlank()) message
        else """
        TTS le message suivant :
        [crying] J'ai faim!
        [excited] Qu'est ce qu'on mange ?
    """.trimIndent()

        //genAiClient com.google.genai.Client à injecter par dépendance
        val response = genAiClient.models.generateContent(
            "gemini-2.5-flash-preview-tts",   // modèle TTS Gemini
            Content.fromParts(Part.fromText(prompt)),
            GenerateContentConfig.builder()
                //Si on souhaite un message système
                //.systemInstruction(Content.fromParts(Part.fromText("")))
                .responseModalities("AUDIO")
                .speechConfig(
                    SpeechConfig.builder()
                        //.languageCode("fr-FR") //Langue utilisée mais normalement auto-détectée
                        .voiceConfig(
                            VoiceConfig.builder()
                                .prebuiltVoiceConfig(
                                    // 30 choix de voix
                                    PrebuiltVoiceConfig.builder().voiceName("Kore").build()
                                ).build()
                        ).build()
                )
                .build()
        )

        // On lit les candidates DIRECTEMENT, sans passer par response.parts() : cette
        // méthode "pratique" valide le finishReason et lève une exception dès qu'il vaut
        // OTHER (fréquent en TTS), alors que l'audio est bien là.
        val pcm = response.candidates().orElse(null)
            ?.firstOrNull()
            ?.content()?.orElse(null)
            ?.parts()?.orElse(null)
            ?.firstNotNullOfOrNull { part ->
                part.inlineData().orElse(null)?.data()?.orElse(null)
            }
            ?: return ResponseEntity.noContent().build()

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("audio/wav"))
            .body(pcmToWav(pcm))
    }

    //genAiClient (com.google.genai.Client) déjà injecté pour le TP TTS

    //http://localhost:8080/image?prompt=Une%20image%20de%20chat
    @GetMapping("/image")
    fun genererImage(@RequestParam prompt: String): ResponseEntity<ByteArray> {

        // On AUTORISE la sortie image (sinon le modèle ne renvoie que du texte)
        val config = GenerateContentConfig.builder()
            .responseModalities("IMAGE")
            .imageConfig(
                ImageConfig.builder()
                    .aspectRatio("4:3")
                    .build()
            )
            .build()

        // Nano Banana = le modèle image de Gemini
        val parts = genAiClient.models.generateContent(
            "gemini-2.5-flash-image", prompt, config
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

    /**
     * Emballe des octets PCM bruts (signés 16-bit, mono, 24 kHz, little-endian) dans un
     * conteneur WAV jouable par un navigateur.
     */
    private fun pcmToWav(
        pcm: ByteArray,
        sampleRate: Int = 24_000,
        channels: Int = 1,
        bitsPerSample: Int = 16
    ): ByteArray {
        val format = AudioFormat(sampleRate.toFloat(), bitsPerSample, channels, true, false)
        val wav = ByteArrayOutputStream()
        AudioSystem.write(
            AudioInputStream(
                ByteArrayInputStream(pcm),
                format,
                pcm.size.toLong() / format.frameSize
            ),
            AudioFileFormat.Type.WAVE,
            wav
        )
        return wav.toByteArray()
    }

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