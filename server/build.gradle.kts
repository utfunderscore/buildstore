plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
    id("com.gradleup.shadow") version "9.0.0-beta9"
}

group = "org.readutf.buildstore"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.52"))

    /**
     * FastAsyncWorldEdit
     */
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }

    /**
     * Annotation Command framework
     */
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.9")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.9")
    implementation("io.github.revxrsal:lamp.brigadier:4.0.0-rc.9")

    implementation(project(":api"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    // Preserve parameter names in the bytecode
    options.compilerArgs.add("-parameters")
}

bukkitPluginYaml {
    name = project.name
    version = project.version.toString()
    main = "org.readutf.buildstore.server.BuildStorePlugin"
    description = "A plugin for BuildStore"
    authors = listOf("utfunderscore")
    apiVersion = "1.17"
    depend = listOf("FastAsyncWorldEdit")
    libraries =
        listOf(
            "io.github.revxrsal:lamp.common:4.0.0-rc.9",
            "io.github.revxrsal:lamp.bukkit:4.0.0-rc.9",
        )
}
