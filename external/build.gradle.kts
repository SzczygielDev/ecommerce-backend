plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm")
    kotlin("plugin.spring") version "1.9.23"
}

repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter:3.2.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.5")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.4.0")
}
tasks.withType<Test> {
    useJUnitPlatform()
}