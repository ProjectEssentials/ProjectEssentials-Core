package com.mairwunnx.projectessentials.core.configuration.commands

import kotlinx.serialization.Serializable

@Serializable
internal data class CommandsConfiguration(
    var nativeReplace: Boolean = true,
    var disableSafelyEnchantLevel: Boolean = false,
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
        var deop: List<String> = listOf(),
        var difficulty: List<String> = listOf(),
        var effect: List<String> = listOf("potion", "eff"),
        var enchant: List<String> = listOf(),
        var experience: List<String> = listOf("exp", "xp"),
        var fill: List<String> = listOf(),
        var function: List<String> = listOf("fn", "fun"),
        var gamemode: List<String> = listOf("gm"),
        var gamerule: List<String> = listOf(),
        var give: List<String> = listOf(),
        var help: List<String> = listOf(),
        var kick: List<String> = listOf(),
        var kill: List<String> = listOf(),
        var list: List<String> = listOf("online")
    )
}
