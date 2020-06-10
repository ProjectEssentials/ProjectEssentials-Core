package com.mairwunnx.projectessentials.core.impl.utils

import com.mairwunnx.projectessentials.core.api.v1.SETTING_NATIVE_COMMAND_REPLACE
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.helpers.getFieldsOf
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.generalConfiguration
import com.mairwunnx.projectessentials.core.impl.nativeMappingsConfiguration
import com.mojang.brigadier.tree.LiteralCommandNode
import com.mojang.brigadier.tree.RootCommandNode
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.literal
import net.minecraft.entity.player.PlayerEntity
import org.apache.logging.log4j.LogManager
import java.util.function.Predicate


object NativeCommandUtils {
    private val logger = LogManager.getLogger()

    private fun isOverridden(name: String) =
        if (generalConfiguration.getBool(SETTING_NATIVE_COMMAND_REPLACE)) name !in overridden else false

    internal fun replaceRequirementPredicates() {
        logger.debug("Replacing native requirement predicates ...")

        CommandAPI.getDispatcher().root.children.asSequence().filter { node ->
            node.name in natives && !isOverridden(node.name)
        }.forEach { node ->
            logger.debug("Replacing requirement predicate for ${node.name}")
            try {
                getFieldsOf(node.javaClass).find { field ->
                    field.name == "requirement"
                }?.let { field ->
                    field.isAccessible = true
                    val cond = nativeMappingsConfiguration.permissions[node.name]?.let { notation ->
                        Predicate { source: CommandSource ->
                            if (source.entity is PlayerEntity) {
                                val pair = notation.split('@')
                                hasPermission(source.asPlayer(), pair.first(), pair.last().toInt())
                            } else true
                        }
                    } ?: let {
                        node.requirement.or { source ->
                            if (source.entity is PlayerEntity) {
                                hasPermission(source.asPlayer(), "native.${node.name}", 4)
                            } else true
                        }
                    }
                    field.set(node, cond)
                } ?: run { logger.debug("Not found requirement predicate field for ${node.name}") }
            } catch (any: Exception) {
                logger.error("Failed to replace requirement predicate for ${node.name}", any)
            }
        }
    }

    internal fun insertNativeAliases() {
        logger.debug("Replacing and inserting native aliases ...")
        CommandAPI.getDispatcher().root.children.filter { node ->
            node.name in natives && !isOverridden(node.name)
        }.forEach { node ->
            node as LiteralCommandNode
            nativeMappingsConfiguration.aliases[node.literal]?.split(',')?.let { aliases ->
                CommandAliases.aliases[node.name] = aliases.toMutableList()
                if (aliases.isNotEmpty()) {
                    aliases.filter { it != node.literal }.forEach { alias ->
                        val lit = literal(alias).requires {
                            node.requirement.test(it)
                        }.executes { return@executes node.command?.run(it) ?: 0 }
                        node.children.forEach { if (node !is RootCommandNode<*>) lit.then(it) }
                        CommandAPI.getDispatcher().register(lit)
                        logger.debug("Alias literal ${lit.literal} registered for ${node.name}")
                    }
                }
            }
        }
    }

    val natives = listOf(
        "advancement", "ban", "ban-ip", "banlist", "bossbar", "clear", "clone", "data", "datapack",
        "debug", "defaultgamemode", "deop", "difficulty", "effect", "enchant", "execute",
        "experience", "fill", "forceload", "function", "gamemode", "gamerule", "give", "help",
        "kick", "kill", "list", "locate", "loot", "me", "msg", "op", "pardon", "particle",
        "playsound", "publish", "recipe", "reload", "replaceitem", "save-all", "save-off",
        "save-on", "say", "schedule", "scoreboard", "seed", "setblock", "setidletimeout",
        "setworldspawn", "spawnpoint", "spectate", "spreadplayers", "stop", "stopsound", "summon",
        "tag", "team", "teammsg", "teleport", "tell", "tellraw", "time", "title", "tp", "trigger",
        "w", "weather", "whitelist", "worldborder", "xp"
    )

    val overridden = listOf(
        "weather", "time", "save-all", "reload", "gamemode", "enchant", "teleport"
    )
}
