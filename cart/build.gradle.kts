plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm")
    kotlin("plugin.spring") version "1.9.23"
}



repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.arrow-kt:arrow-core:1.2.4")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.4")
    implementation("com.trendyol:kediatr-core:3.0.0")
    implementation("com.trendyol:kediatr-spring-starter:3.0.0")

    implementation("org.springframework.boot:spring-boot-starter:3.2.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.5")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.4.0")


    implementation("org.jetbrains.exposed:exposed-core:0.55.0")
    runtimeOnly("org.jetbrains.exposed:exposed-jdbc:0.55.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.55.0")
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.55.0")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.0")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.0")
    implementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("io.mockk:mockk:1.13.11")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation(project(mapOf("path" to ":product")))
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Test> {
    useJUnitPlatform()
}