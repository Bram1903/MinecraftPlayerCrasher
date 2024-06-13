plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "com.deathmotion.playercrasher"
version = "2.5.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    disableAutoTargetJvm()
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.3.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 8
    }

    shadowJar {
        minimize()
        archiveFileName.set("${project.name}-${project.version}.jar")

        relocate(
            "net.kyori.adventure.text.serializer.gson",
            "io.github.retrooper.packetevents.adventure.serializer.gson"
        )
        relocate(
            "net.kyori.adventure.text.serializer.legacy",
            "io.github.retrooper.packetevents.adventure.serializer.legacy"
        )
    }

    // 1.8.8 - 1.16.5 = Java 8
    // 1.17           = Java 16
    // 1.18 - 1.20.4  = Java 17
    // 1-20.5+        = Java 21
    val version = "1.20.6"
    val javaVersion = 21

    val requiredPlugins = runPaper.downloadPluginsSpec {
        url("https://ci.codemc.io/job/retrooper/job/packetevents/426/artifact/spigot/build/libs/packetevents-spigot-2.3.1-SNAPSHOT.jar")
    }

    val jvmArgsExternal = listOf(
        "-Dcom.mojang.eula.agree=true"
    )

    runServer {
        minecraftVersion(version)
        runDirectory.set(file("server/paper/$version"))

        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        })

        downloadPlugins.from(requiredPlugins)
        downloadPlugins {
            url("https://ci.lucko.me/job/spark/410/artifact/spark-bukkit/build/libs/spark-1.10.65-bukkit.jar")
            url("https://download.luckperms.net/1543/bukkit/loader/LuckPerms-Bukkit-5.4.130.jar")
            url("https://github.com/ViaVersion/ViaVersion/releases/download/4.10.2/ViaVersion-4.10.2.jar")
            url("https://github.com/ViaVersion/ViaBackwards/releases/download/4.10.2/ViaBackwards-4.10.2.jar")
            url("https://github.com/ViaVersion/ViaRewind/releases/download/3.1.2/ViaRewind-3.1.2.jar")
        }

        jvmArgs = jvmArgsExternal
    }

    runPaper.folia.registerTask() {
        minecraftVersion(version)
        runDirectory.set(file("server/folia/$version"))

        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        })

        downloadPlugins.from(requiredPlugins)

        jvmArgs = jvmArgsExternal
    }
}