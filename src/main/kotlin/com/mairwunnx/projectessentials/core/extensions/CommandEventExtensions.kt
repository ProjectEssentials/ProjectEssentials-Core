@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.extensions

import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.event.CommandEvent

/**
 * Return command name as string.
 *
 * Example: player execute command **`/heal MairwunNx`**,
 * then you get **`heal`** as string.
 * @since 1.14.4-1.0.0.0
 */
val CommandEvent.commandName: String
    get() = this.executedCommand
        .replace("/", "").split(" ")[0]

/**
 * Return fully executed command as string.
 *
 * Example: player execute command **`/heal MairwunNx`**,
 * then you get **`/heal MairwunNx`** as string.
 * @since 1.14.4-1.0.0.0
 */
val CommandEvent.executedCommand: String
    get() = this.parseResults.reader.string

/**
 * Return true if source is player.
 * @since 1.14.4-1.2.1
 */
val CommandEvent.isPlayerSender: Boolean
    get() = this.source.entity is ServerPlayerEntity

/**
 * Return **`ServerPlayerEntity?`** class instance from
 * **`CommandEvent`** class instance. If source is server
 * then return null.
 * @since 1.14.4-1.0.0.0
 */
val CommandEvent.player: ServerPlayerEntity?
    get() = if (this.isPlayerSender) this.source.asPlayer() else null

/**
 * Return command **`source`** from **`CommandEvent`**
 * class instance.
 * @since 1.14.4-1.0.0.0
 */
val CommandEvent.source: CommandSource
    get() = this.parseResults.context.source
