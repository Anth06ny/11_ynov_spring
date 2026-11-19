import org.springframework.boot.gradle.tasks.bundling.BootJar
import sun.jvmstat.monitor.MonitoredVmUtil.mainClass

plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.3.21"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "MessagerieService"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

//Si on souhaite modifier les paramètre de notre executable
tasks.named<BootJar>("bootJar") {
    archiveFileName.set("monChatServer.jar")
    destinationDirectory.set(file("$rootDir")) //pour le mettre à la racine
    //S'il y a plusieurs main il faut bien mettre celui qui lance le serveur
    mainClass.set("org.example.messagerieservice.MessagerieServiceApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    runtimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    //Si on veut la console H2
    runtimeOnly("org.springframework.boot:spring-boot-h2console")
    testImplementation(kotlin("test"))

}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
