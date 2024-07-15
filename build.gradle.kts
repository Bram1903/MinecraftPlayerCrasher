plugins {
    playercrasher.`java-conventions`
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.run.velocity)
}

group = "com.deathmotion.playercrasher"
description = "A plugin that prevents hackers and modders from seeing the health of other players."
version = "3.0.0-SNAPSHOT"

dependencies {
    implementation(project(":common"))
    implementation(project(":platforms:bukkit"))
    implementation(project(":platforms:velocity"))
    implementation(project(":platforms:bungeecord"))
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${project.version}.jar"
        archiveClassifier = null

        relocate(
            "net.kyori.adventure.text.serializer.gson",
            "io.github.retrooper.packetevents.adventure.serializer.gson"
        )
        relocate(
            "net.kyori.adventure.text.serializer.legacy",
            "io.github.retrooper.packetevents.adventure.serializer.legacy"
        )

        manifest {
            attributes["Implementation-Version"] = rootProject.version
        }
    }

    assemble {
        dependsOn(shadowJar)
    }

    // 1.8.8 - 1.16.5 = Java 8
    // 1.17           = Java 16
    // 1.18 - 1.20.4  = Java 17
    // 1-20.5+        = Java 21
    val version = "1.21"
    val javaVersion = JavaLanguageVersion.of(21)

    val jvmArgsExternal = listOf(
        "-Dcom.mojang.eula.agree=true"
    )

    val sharedBukkitPlugins = runPaper.downloadPluginsSpec {
        url("https://ci.codemc.io/job/retrooper/job/packetevents/lastSuccessfulBuild/artifact/spigot/build/libs/packetevents-spigot-2.4.1-SNAPSHOT.jar")
        url("https://github.com/ViaVersion/ViaVersion/releases/download/5.0.1/ViaVersion-5.0.1.jar")
        url("https://github.com/ViaVersion/ViaBackwards/releases/download/5.0.1/ViaBackwards-5.0.1.jar")
    }

    runServer {
        minecraftVersion(version)
        runDirectory = file("run/paper/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = javaVersion
        }

        downloadPlugins {
            from(sharedBukkitPlugins)
            url("https://ci.lucko.me/job/spark/422/artifact/spark-bukkit/build/libs/spark-1.10.77-bukkit.jar")
            url("https://download.luckperms.net/1552/bukkit/loader/LuckPerms-Bukkit-5.4.137.jar")
        }

        jvmArgs = jvmArgsExternal
    }

    runPaper.folia.registerTask() {
        minecraftVersion(version)
        runDirectory = file("run/folia/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = javaVersion
        }

        downloadPlugins {
            from(sharedBukkitPlugins)
        }

        jvmArgs = jvmArgsExternal
    }

    runVelocity {
        velocityVersion("3.3.0-SNAPSHOT")
        runDirectory = file("run/velocity/")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = javaVersion
        }

        downloadPlugins {
            url("https://ci.codemc.io/job/retrooper/job/packetevents/lastSuccessfulBuild/artifact/velocity/build/libs/packetevents-velocity-2.4.1-SNAPSHOT.jar")
        }
    }
}