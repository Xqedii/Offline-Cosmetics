plugins {
    id("net.labymod.labygradle")
    id("net.labymod.labygradle.addon")
}

val versions = providers.gradleProperty("net.labymod.minecraft-versions").get().split(";")

group = "dev.xqedii"
version = providers.environmentVariable("VERSION").getOrElse("1.0.0")

labyMod {
    defaultPackageName = "dev.xqedii"

    minecraft {
        registerVersion(versions.toTypedArray()) {
            runs {
                getByName("client") {
                }
            }
        }
    }

    addonInfo {
        namespace = "xqedii"
        displayName = "Offline Cosmetics"
        author = "Xqedii"
        description = "Restores client-side visibility of cosmetics on cracked servers."
        minecraftVersion = "*"
        version = rootProject.version.toString()
    }
}

subprojects {
    plugins.apply("net.labymod.labygradle")
    plugins.apply("net.labymod.labygradle.addon")

    group = rootProject.group
    version = rootProject.version
}