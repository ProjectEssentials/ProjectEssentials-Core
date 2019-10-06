@file:Suppress("unused")

package com.mairwunnx.projectessentialscore.extensions

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
        .replace("/", "")
        .split(" ")[0]

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
 * Return **`ServerPlayerEntity`** class instance from
 * **`CommandEvent`** class instance.
 * @since 1.14.4-1.0.0.0
 */
val CommandEvent.player: ServerPlayerEntity
    get() = this.parseResults.context.source.asPlayer()

/**
 * Return command **`source`** from **`CommandEvent`**
 * class instance.
 * @since 1.14.4-1.0.0.0
 */
val CommandEvent.source: CommandSource
    get() = this.parseResults.context.source
