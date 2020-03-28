package com.mairwunnx.projectessentials.core.impl.configurations

import kotlinx.serialization.Serializable

@Serializable
data class NativeAliasesConfigurationModel(
    val aliases: Aliases = Aliases()
) {
    @Serializable
    data class Aliases(
        val advancement: List<String> = listOf("achievements", "ac"),
        val ban: List<String> = listOf(),
        val banip: List<String> = listOf("banip"),
        val banlist: List<String> = listOf(),
        val bossbar: List<String> = listOf(),
        val clear: List<String> = listOf("ci", "clearinventory"),
        val clone: List<String> = listOf(),
        val datapack: List<String> = listOf(),
        val debug: List<String> = listOf("dbg"),
        val defaultgamemode: List<String> = listOf("defaultgm"),
        val deop: List<String> = listOf(),
        val difficulty: List<String> = listOf(),
        val effect: List<String> = listOf("potion", "eff"),
        val enchant: List<String> = listOf(),
        val experience: List<String> = listOf("exp", "xp"),
        val fill: List<String> = listOf(),
        val function: List<String> = listOf("fn", "fun"),
        val gamemode: List<String> = listOf("gm"),
        val gamerule: List<String> = listOf("gr"),
        val give: List<String> = listOf(),
        val help: List<String> = listOf(),
        val kick: List<String> = listOf(),
        val kill: List<String> = listOf(),
        val list: List<String> = listOf("online"),
        val locate: List<String> = listOf("find", "where"),
        val me: List<String> = listOf("action", "describe"),
        val op: List<String> = listOf(),
        val pardon: List<String> = listOf("unban"),
        val pardonip: List<String> = listOf("unbanip", "pardonip"),
        val particle: List<String> = listOf(),
        val publish: List<String> = listOf("openlan"),
        val recipe: List<String> = listOf(),
        val replaceItem: List<String> = listOf(),
        val saveall: List<String> = listOf("saveall"),
        val saveoff: List<String> = listOf("saveoff"),
        val saveon: List<String> = listOf("saveon"),
        val spawnpoint: List<String> = listOf("respawnhere"),
        val summon: List<String> = listOf("spawnmob"),
        val tellraw: List<String> = listOf("tr"),
        val worldborder: List<String> = listOf("wb")
    )
}
