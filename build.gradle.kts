plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "com.deathmotion.playercrasher"
version = "2.2.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    compileOnly("com.github.retrooper.packetevents:spigot:2.2.1")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        minimize()
        archiveFileName.set("${project.name}-${project.version}.jar")

        relocate("co.aikar.commands", "com.deathmotion.playercrasher.shaded.acf")
        relocate("co.aikar.locales", "com.deathmotion.playercrasher.shaded.locales")
    }

    runServer {
        // The version of the server to run
        val version = "1.20.4"

        minecraftVersion(version)
        runDirectory.set(file("server/$version"))

        // 1.8.8 - 1.16.5 = Java 8
        // 1.17           = Java 16
        // 1.18 - 1.20.4  = Java 17
        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        })

        downloadPlugins {
            url("https://ci.codemc.io/job/retrooper/job/packetevents/lastBuild/artifact/spigot/build/libs/packetevents-spigot-2.2.1.jar")
            url("https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar")
            url("https://ci.lucko.me/job/spark/400/artifact/spark-bukkit/build/libs/spark-1.10.59-bukkit.jar")
            url("https://download.luckperms.net/1530/bukkit/loader/LuckPerms-Bukkit-5.4.117.jar")
        }

        jvmArgs = listOf(
            "-Dcom.mojang.eula.agree=true"
        )
    }
}