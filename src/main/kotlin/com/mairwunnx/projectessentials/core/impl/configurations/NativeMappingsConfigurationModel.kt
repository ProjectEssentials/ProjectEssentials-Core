package com.mairwunnx.projectessentials.core.impl.configurations

import kotlinx.serialization.Serializable

@Serializable
data class NativeMappingsConfigurationModel(
    val permissions: MutableMap<String, String> = mutableMapOf(
        "_comment" to "Hello there! You can change permissions mappings there, in format \"command\":\"node@op-level\". By removing command there, Project Essentials will generate permissions node automatically bases on command name, for command advancements will generated permissions `native.advancements` with op level provided in `https://minecraft.gamepedia.com/Commands`.",
        "ban" to "native.moderation.ban@3",
        "ban-ip" to "native.moderation.banip@3",
        "banlist" to "native.moderation.banlist@3",
        "pardon" to "native.moderation.pardon@3",
        "pardon-ip" to "native.moderation.pardonip@3",
        "kick" to "native.moderation.kick@3",
        "deop" to "native.stuff.operator.remove@3",
        "op" to "native.stuff.operator.add@3",
        "me" to "native.messaging.me@0",
        "message" to "native.messaging.message@0",
        "teammsg" to "native.messaging.teammsg@0",
        "playsound" to "native.sound.play@2",
        "stopsound" to "native.sound.stop@2",
        "reload" to "native.server.reload@2",
        "stop" to "native.server.stop@4",
        "save-off" to "native.save.off@4",
        "save-on" to "native.save.on@4",
        "say" to "native.messaging.say@1"
    ),
    val aliases: MutableMap<String, String> = mutableMapOf(
        "_comment" to "There are command aliases, leave this empty if you want commands without aliases, if you want many aliases, enumerate that with `,`.",
        "clear" to "ci",
        "list" to "online",
        "locate" to "find,where",
        "pardon" to "unban",
        "gamerule" to "gr",
        "summon" to "spawnmob",
        "pardonip" to "unbanip",
        "sun" to "weatherclear,clearsky,sky",
        "thunder" to "storm,goodweather",
        "noon" to "midday,noonday",
        "sunset" to "dusk,sundown,evening",
        "sunrise" to "dawn,morning,morn"
    )
)
