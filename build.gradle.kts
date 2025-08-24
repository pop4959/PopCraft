import java.util.*

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "8.3.9"
}

group = project.property("pluginGroup")!!
version = project.property("pluginVersion")!!

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
    jar {
        archiveClassifier.set("noshade")
    }
    shadowJar {
        minimize()
        archiveClassifier.set("")
        archiveFileName.set("${rootProject.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}-${project.version}.jar")
    }
    build {
        dependsOn(shadowJar)
    }
}

repositories {
    mavenCentral()
    // Paper
    maven("https://repo.papermc.io/repository/maven-public/")
    // Essentials
    maven("https://repo.essentialsx.net/releases/")
    maven("https://repo.essentialsx.net/snapshots/")
    // Vault, NuVotifier
    maven("https://jitpack.io")
}

dependencies {
    testCompileOnly("junit:junit:4.13.2")
    compileOnly("io.papermc.paper:paper-api:${project.property("apiVersion")}")
    compileOnly("net.essentialsx:EssentialsX:2.22.0-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.github.NuVotifier.NuVotifier:nuvotifier-api:2.7.2")
    compileOnly("com.github.NuVotifier.NuVotifier:nuvotifier-bukkit:2.7.2")
    compileOnly("net.luckperms:api:5.5")
}
