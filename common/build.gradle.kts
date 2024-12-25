architectury {
    common("fabric")
    platformSetupLoomIde()
}

dependencies {

    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")

    modCompileOnly("com.cobblemon:mod:${property("cobblemon_version")}")
    // alL fabric dependencies:
    modCompileOnly("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

    modImplementation("dev.architectury:architectury:${property("architectury_version")}")
    modImplementation("ca.landonjw.gooeylibs:fabric-api-repack:${property("gooeylibs_version")}")

    modImplementation(files("libs/CobbleUtils-common-1.1.3.jar"))

    // Database
    api("org.mongodb:mongodb-driver-sync:${property("mongodb_version")}")

    // Kyori Adventure
    api("net.kyori:adventure-text-serializer-gson:${property("kyori_version")}")
    api("net.kyori:adventure-text-minimessage:${property("kyori_version")}")

    // Lombok
    annotationProcessor("org.projectlombok:lombok:${property("lombok_version")}")
    implementation("org.projectlombok:lombok:${property("lombok_version")}")

    //Discord
    api("club.minnced:discord-webhooks:${property("discord_webhooks_version")}")
    implementation("net.objecthunter:exp4j:0.4.8")
}



