package com.mairwunnx.projectessentials.core.configuration.commands

import kotlinx.serialization.Serializable

@Serializable
internal data class CommandsConfiguration(
    var nativeReplace: Boolean = true,
    var disableSafelyEnchantLevel: Boolean = false,
    var locateFindRadius: Int = 100,
    var aliases: Aliases = Aliases()
) {
    @Serializable
    data class Aliases(
        var advancement: List<String> = listOf("achievements", "ac"),
        var ban: List<String> = listOf(),
        var banip: List<String> = listOf("banip"),
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
        var list: List<String> = listOf("online"),
        var locate: List<String> = listOf("find", "where"),
        var me: List<String> = listOf("action", "describe"),
        var op: List<String> = listOf(),
        var pardon: List<String> = listOf("unban"),
        var pardonip: List<String> = listOf("unbanip", "pardonip"),
        var particle: List<String> = listOf(),
        var publish: List<String> = listOf("openlan"),
        var recipe: List<String> = listOf(),
        var replaceItem: List<String> = listOf(),
        var saveall: List<String> = listOf("saveall"),
        var saveoff: List<String> = listOf("saveoff"),
        var saveon: List<String> = listOf("saveon"),
        var spawnpoint: List<String> = listOf("respawnhere"),
        var summon: List<String> = listOf("spawnmob"),
        var tellraw: List<String> = listOf("tr"),
        var worldborder: List<String> = listOf("wb")
    )
}
