import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
}

group = "pl.szczygieldev"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(project(mapOf("path" to ":product")))
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation(project(":shared"))
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.5")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.1")
    implementation("io.arrow-kt:arrow-core:1.2.4")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.4")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.0")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.0")
    implementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("io.mockk:mockk:1.13.11")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0-RC")
    implementation("org.springframework.boot:spring-boot-starter-mail:3.3.4")
    implementation("com.github.spullara.mustache.java:compiler:0.9.14")

    implementation("com.h2database:h2:2.3.232")

    implementation("org.jetbrains.exposed:exposed-core:0.55.0")
    runtimeOnly("org.jetbrains.exposed:exposed-jdbc:0.55.0")
    runtimeOnly("org.jetbrains.exposed:exposed-kotlin-datetime:0.55.0")
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.55.0")

    implementation("com.trendyol:kediatr-core:3.0.0")
    implementation("com.trendyol:kediatr-spring-starter:3.0.0")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.3.5")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
