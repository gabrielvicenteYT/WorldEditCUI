plugins {
    id("fabric-loom") version "0.2.4-SNAPSHOT"
}

group = "com.mumfrey.worldeditcui"
version = "1.14.2_01"

val minecraftVersion = "1.14.2"
val yarnVersion = "1.14.2+build.7"
val fabricLoaderVersion = "0.4.8+build.155"
val fabricApiVersion = "0.3.0+build.183"
val modmenuVersion = "1.6.2-92"

dependencies {
    add("minecraft", "com.mojang:minecraft:$minecraftVersion")
    add("mappings", "net.fabricmc:yarn:$yarnVersion")
    add("modCompile", "net.fabricmc:fabric-loader:$fabricLoaderVersion")
    add("modCompile", "net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    add("modCompile", "io.github.prospector.modmenu:ModMenu:$modmenuVersion")
}

val processResources by tasks.getting(ProcessResources::class) {
    inputs.property("version", project.version)

    from(sourceSets["main"].resources.srcDirs) {
        include("fabric.mod.json")
        expand("version" to project.version)
    }
}