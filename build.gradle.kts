import org.slf4j.event.Level

plugins {
    idea
    alias(libs.plugins.moddev)
}

val modId = providers.gradleProperty("mod_id")
val modVersion = providers.gradleProperty("mod_version")
val modGroupId = providers.gradleProperty("mod_group_id")
val modName = providers.gradleProperty("mod_name")
val modLicense = providers.gradleProperty("mod_license")
val modAuthors = providers.gradleProperty("mod_authors")
val modDescription = providers.gradleProperty("mod_description")

val minecraftVersion = libs.versions.minecraft.exact
val minecraftVersionRange = libs.versions.minecraft.range
val neoVersion = libs.versions.neoforge.exact
val neoVersionRange = libs.versions.neoforge.range
val parchmentMappingsVersion = libs.versions.parchemnt.mappings
val parchmentMinecraftVersion = libs.versions.parchment.minecraft

val simpleMagnetsRange = libs.versions.simplemagnets.range

version = modVersion.get()
group = modGroupId.get()

base {
    archivesName = modId
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    version = neoVersion.get()

    parchment {
        mappingsVersion = parchmentMappingsVersion
        minecraftVersion = parchmentMinecraftVersion
    }

    runs {
        create("client") {
            client()
        }

        create("server") {
            server()
            programArgument("--nogui")
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            systemProperty("neoforge.enabledGameTestNamespaces", modId.get())
            logLevel = Level.DEBUG
        }
    }

    mods {
        create(modId.get()) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

repositories {
    exclusiveContent {
        forRepository {
            maven("https://cursemaven.com")
        }
        filter {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    implementation(libs.supermartijn642.config)
    implementation(libs.supermartijn642.core)
    implementation(libs.simplemagnets)
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    description = "Generate mod metadata"

    val replaceProperties = mapOf(
        "minecraft_version" to minecraftVersion.get(),
        "minecraft_version_range" to minecraftVersionRange.get(),
        "neo_version" to neoVersion.get(),
        "neo_version_range" to neoVersionRange.get(),
        "mod_id" to modId.get(),
        "mod_name" to modName.get(),
        "mod_license" to modLicense.get(),
        "mod_version" to modVersion.get(),
        "mod_authors" to modAuthors.get(),
        "mod_description" to modDescription.get(),
        "simplemagnets_range" to simpleMagnetsRange.get()
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

sourceSets.main {
    resources.srcDir(generateModMetadata)
}

neoForge.ideSyncTask(generateModMetadata)

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}