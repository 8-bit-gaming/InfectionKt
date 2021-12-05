import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    id("io.papermc.paperweight.userdev") version "1.3.1"
    id("xyz.jpenilla.run-paper") version "1.0.5" // Adds runServer and runMojangMappedServer tasks for testing
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "cf.pixelinc"
version = "1.0.0-SNAPSHOT"
description = "A minecraft infection plugin written in kotlin"

java {
    disableAutoTargetJvm()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).apply {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

dependencies {
    paperDevBundle("1.18-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.0")
}

tasks {
    shadowJar {
        minimize()
        archiveClassifier.set(null as String?)
        archiveBaseName.set(project.name) // Use uppercase name for final jar

        val prefix = "${project.group}.${project.name.toLowerCase()}.lib"
        sequenceOf(
            "kotlin",
        ).forEach { pkg ->
            relocate(pkg, "$prefix.$pkg")
        }

        dependencies {
            exclude(dependency("org.jetbrains:annotations"))
        }
    }
    // Run reobfJar on build
    build {
        dependsOn(reobfJar)
        dependsOn(shadowJar)
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "cf.pixelinc.InfectionPlugin"
    apiVersion = "1.18"
    authors = listOf("Author")

    commands {
        register("infected") {
            description = "Main command for plugin"
        }
    }
}