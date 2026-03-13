import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("xyz.jpenilla.run-paper") version "3.0.2" // Adds runServer and runMojangMappedServer tasks for testing
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.0" // Generates plugin.yml based on the Gradle config
}

group = "me.eren.ipblocker"
version = "1.0.0"
description = "Block IPs before they appear on console"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

dependencies {
    paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
      options.release = 21
    }
    javadoc {
      options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}

// Configure plugin.yml generation
// - name, version, and description are inherited from the Gradle project.
bukkitPluginYaml {
    main = "me.eren.ipblocker.IPBlocker"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.add("karakulak")
    apiVersion = "1.21.10"
}
