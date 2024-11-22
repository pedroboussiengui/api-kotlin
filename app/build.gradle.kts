/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.10.2/userguide/building_java_projects.html in the Gradle documentation.
 */

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("io.mockk:mockk:1.9.3")

    // This dependency is used by the application.
    implementation(libs.guava)

    implementation("io.javalin:javalin:6.3.0") // javalin
    implementation("org.slf4j:slf4j-simple:2.0.16") // logs
    implementation("com.google.code.gson:gson:2.11.0") // gson json mapper
    implementation("org.valiktor:valiktor-core:0.12.0") // valiktor
    implementation("org.ktorm:ktorm-core:3.3.0") // ktorm database
    implementation("org.xerial:sqlite-jdbc:3.41.2.1") // sqlite connector (No suitable driver found)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    // Define the main class for the application.
    mainClass = "org.example.AppKt"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
