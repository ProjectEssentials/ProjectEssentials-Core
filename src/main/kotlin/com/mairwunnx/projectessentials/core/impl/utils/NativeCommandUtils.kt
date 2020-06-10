package com.mairwunnx.projectessentials.core.impl.utils

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.helpers.getFieldsOf
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.nativeMappingsConfiguration
import net.minecraft.entity.player.PlayerEntity
import org.apache.logging.log4j.LogManager
import java.util.function.Predicate

object NativeCommandUtils {
    private val logger = LogManager.getLogger()

    internal fun replaceRequirementPredicates() {
        logger.debug("Replacing native requirement predicates ...")

        CommandAPI.getDispatcher().root.children.asSequence().filter { node ->
            node.name in natives && node.name !in overridden
        }.forEach { node ->
            logger.debug("Replacing requirement predicate for ${node.name}")
            try {
                getFieldsOf(node.javaClass).find { field ->
                    field.name == "requirement"
                }?.let { field ->
                    field.isAccessible = true
                    val cond = nativeMappingsConfiguration.permissions[node.name]?.let { notation ->
                        Predicate { source ->
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

    val overridden = mutableListOf(
        "enchant", "gamemode", "op", "save-all", "teleport", "time", "tp", "weather"
    )
}
