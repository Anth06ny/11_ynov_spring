package org.example.messagerieservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class MessagerieServiceApplication

fun main(args: Array<String>) {
    runApplication<MessagerieServiceApplication>(*args)
}
