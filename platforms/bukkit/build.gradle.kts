plugins {
    antihealthindicator.`java-conventions`
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":common"))
    implementation(libs.adventure.platform.bukkit)
    compileOnly(libs.paper)
    compileOnly(libs.packetevents.spigot)
}