@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.RootCommandNode
import net.minecraft.command.CommandSource
import net.minecraft.command.arguments.EntityArgument
import net.minecraft.entity.Entity
import net.minecraft.entity.player.ServerPlayerEntity

/**
 * Class for interacting with command api.
 * @since 2.0.0-SNAPSHOT.1.
 */
object CommandAPI {
    private lateinit var registeredCommands: RootCommandNode<CommandSource>
    private lateinit var dispatcher: CommandDispatcher<CommandSource>
    private var dispatcherAssigned = false

    /**
     * Internal function. Assign dispatcher root
     * for removing command. Must be called before
     * any processor initializing.
     * @param dispatcher command dispatcher.
     * @since 2.0.0-SNAPSHOT.1.
     */
    /*
        This is a crutch to replace vanilla commands,
         and to gain access to the dispatcher root.
     */
    @Suppress("UNCHECKED_CAST")
    internal fun assignDispatcherRoot(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        val root = dispatcher.javaClass.getDeclaredField("root")
        root.isAccessible = true
        registeredCommands = root.get(dispatcher) as RootCommandNode<CommandSource>
    }

    /**
     * Assign dispatcher for command registering.
     * @param dispatcher command dispatcher.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun assignDispatcher(
        dispatcher: CommandDispatcher<CommandSource>
    ) {
        if (!dispatcherAssigned) {
            dispatcherAssigned = true
            this.dispatcher = dispatcher
        }
    }

    /**
     * @return server command dispatcher.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getDispatcher() = dispatcher

    /**
     * @return true if dispatcher assigned.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun isAssignedDispatcher() = dispatcherAssigned

    /**
     * @return all installed and checked commands.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getAllCommands() = CommandProcessor.getCommands()

    /**
     * Remove already registered command with
     * specified name.
     * @param command command name to remove without `/`.
     * @return true if command removed, false otherwise.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun removeCommand(command: String): Boolean =
        registeredCommands.children.removeIf { it.name == command }

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return true if string exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getStringExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        StringArgumentType.getString(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return string from command.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getString(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): String = StringArgumentType.getString(context, argumentName)

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return true if int exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getIntExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        IntegerArgumentType.getInteger(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return int from command.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getInt(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Int = IntegerArgumentType.getInteger(context, argumentName)

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return true if bool exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getBoolExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        BoolArgumentType.getBool(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return bool from command.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getBool(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = BoolArgumentType.getBool(context, argumentName)

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return true if entity exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getEntityExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        EntityArgument.getEntity(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return entity from command.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getEntity(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Entity = EntityArgument.getEntity(context, argumentName)

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return true if entities exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getEntitiesExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        EntityArgument.getEntities(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return entities from command.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getEntities(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): MutableCollection<out Entity> = EntityArgument.getEntities(context, argumentName)

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return true if player exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getPlayerExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        EntityArgument.getPlayer(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return player from command.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getPlayer(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): ServerPlayerEntity = EntityArgument.getPlayer(context, argumentName)

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return true if players exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getPlayersExisting(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): Boolean = try {
        EntityArgument.getPlayers(context, argumentName)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    /**
     * @param context command context.
     * @param argumentName argument name.
     * @return players from command.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getPlayers(
        context: CommandContext<CommandSource>,
        argumentName: String
    ): MutableCollection<ServerPlayerEntity> = EntityArgument.getPlayers(context, argumentName)
}
