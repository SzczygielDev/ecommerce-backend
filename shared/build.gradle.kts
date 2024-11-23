plugins {
    kotlin("jvm")
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.springframework.boot:spring-boot-starter:3.2.5")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(17)
}