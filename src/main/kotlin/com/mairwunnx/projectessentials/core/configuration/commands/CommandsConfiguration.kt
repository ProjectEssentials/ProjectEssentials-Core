package com.mairwunnx.projectessentials.core.configuration.commands

import kotlinx.serialization.Serializable

@Serializable
internal data class CommandsConfiguration(
    var nativeReplace: Boolean = true,
    var aliases: Aliases = Aliases()
) {
    @Serializable
    data class Aliases(
        var advancement: List<String> = listOf("achievements", "ac"),
        var ban: List<String> = listOf(),
        var banip: List<String> = listOf(),
        var banlist: List<String> = listOf(),
        var bossbar: List<String> = listOf(),
        var clear: List<String> = listOf("ci", "clearinventory"),
        var clone: List<String> = listOf(),
        var datapack: List<String> = listOf(),
        var debug: List<String> = listOf("dbg"),
        var defaultgamemode: List<String> = listOf("defaultgm"),
        var deop: List<String> = listOf()
    )
}
