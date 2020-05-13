@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.extensions

import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI.getLocalizedString
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mojang.brigadier.context.CommandContext
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandSource
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.HoverEvent
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.server.ServerLifecycleHooks
import java.io.File

private val generalConfiguration by lazy {
    ConfigurationAPI.getConfigurationByName<GeneralConfiguration>("general")
}

/**
 * Plays sound to player at player position on both sides.
 * @param player target player to play sound.
 * @param sound sound to play.
 * @param category sound category, default value is [SoundCategory.AMBIENT].
 * @param volume sound volume, default value is `1.0f`.
 * @param pitch sound pitch, default value is `1.0f`.
 */
fun ServerPlayerEntity.playSound(
    player: PlayerEntity,
    sound: SoundEvent,
    category: SoundCategory = SoundCategory.AMBIENT,
    volume: Float = 1.0f,
    pitch: Float = 1.0f
) {
    val pos = player.positionVec
    DistExecutor.runWhenOn(Dist.CLIENT) {
        Runnable {
            Minecraft.getInstance().world.playSound(
                pos.x, pos.y + player.eyeHeight.toDouble(), pos.z,
                sound, category, volume, pitch, false
            )
            player.entity.playSound(sound, volume, pitch)
        }
    }
    DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
        Runnable {
            player.world.playSound(
                null, pos.x, pos.y + player.eyeHeight.toDouble(), pos.z,
                sound, category, volume, pitch
            )
        }
    }
}

/**
 * @return true if command sender is player.
 * @since 2.0.0-SNAPSHOT.1.
 */
fun CommandContext<CommandSource>.isPlayerSender() = this.source.entity is ServerPlayerEntity

/**
 * @return player if sender is player, if sender is
 * entity or server then return null.
 * @since 2.0.0-SNAPSHOT.1.
 */
fun CommandContext<CommandSource>.getPlayer() =
    if (this.isPlayerSender()) this.source.asPlayer() else null

/**
 * @return if command source is player then nickname
 * from CommandContext. If command source is server
 * then return `#server`.
 * @since 2.0.0-SNAPSHOT.1.
 */
fun CommandContext<CommandSource>.playerName(): String =
    if (this.isPlayerSender()) this.source.asPlayer().name.string else "#server"

/**
 * Return command name as string.
 *
 * Example: player execute command **`/heal MairwunNx`**,
 * then you get **`heal`** as string.
 * @since 2.0.0-SNAPSHOT.1.
 */
val CommandEvent.commandName
    get() = this.executedCommand.replace("/", "").split(" ")[0]

/**
 * Return fully executed command as string.
 *
 * Example: player execute command **`/heal MairwunNx`**,
 * then you get **`/heal MairwunNx`** as string.
 * @since 2.0.0-SNAPSHOT.1.
 */
val CommandEvent.executedCommand: String get() = this.parseResults.reader.string

/**
 * @return true if source is player.
 * @since 2.0.0-SNAPSHOT.1.
 */
fun CommandEvent.isPlayerSender() = this.source.entity is ServerPlayerEntity

/**
 * Return **`ServerPlayerEntity?`** class instance from
 * **`CommandEvent`** class instance. If source is server
 * then return null.
 * @since 2.0.0-SNAPSHOT.1.
 */
fun CommandEvent.getPlayer() = if (this.isPlayerSender()) this.source.asPlayer() else null

/**
 * Return command **`source`** from **`CommandEvent`**
 * class instance.
 * @since 2.0.0-SNAPSHOT.1.
 */
val CommandEvent.source: CommandSource get() = this.parseResults.context.source

/**
 * @param player server player entity instance.
 * @param safeLocalization if safe localization
 * enabled then will be used non-client localization.
 * @param l10n localization string for indexing localized
 * string.
 * @param args additional arguments for localized string.
 * @return HoverEvent instance with your localized string.
 * @since 2.0.0-SNAPSHOT.1.
 */
fun hoverEventFrom(
    player: ServerPlayerEntity,
    safeLocalization: Boolean = generalConfiguration.getBool(SETTING_LOC_ENABLED),
    l10n: String,
    vararg args: String
) = if (safeLocalization) {
    HoverEvent(
        HoverEvent.Action.SHOW_TEXT,
        TextComponentUtils.toTextComponent {
            getLocalizedString(player.language, l10n, *args)
        }
    )
} else {
    HoverEvent(
        HoverEvent.Action.SHOW_TEXT,
        TranslationTextComponent(l10n, args)
    )
}

/**
 * @param player server player entity instance.
 * @param safeLocalization if safe localization
 * enabled then will be used non-client localization.
 * @param l10n localization string for indexing localized
 * string.
 * @param args additional arguments for localized string.
 * @return ITextComponent implements class instance with
 * your localized string.
 * @since 2.0.0-SNAPSHOT.1.
 */
fun textComponentFrom(
    player: ServerPlayerEntity,
    safeLocalization: Boolean = generalConfiguration.getBool(SETTING_LOC_ENABLED),
    l10n: String,
    vararg args: String
): ITextComponent = TextComponentUtils.toTextComponent {
    if (safeLocalization) {
        getLocalizedString(player.language, l10n, *args)
    } else {
        TranslationTextComponent(l10n, args).formattedText
    }
}

/**
 * Capitalize each word in string.
 * @return capitalized each word string.
 * @since 2.0.0-SNAPSHOT.1.
 */
fun String.capitalizeWords() = split(" ").joinToString(" ") { it.capitalize() }

/**
 * Return empty string.
 * @since 2.0.0-SNAPSHOT.1.
 */
val String.Companion.empty get() = ""

/**
 * Returns world name.
 *
 * In case with server:
 * ```
 *   - world directory is `world` (like default name),
 *   then you will get `world` as world name.
 * ```
 *
 * In case with client:
 * ```
 *   - world directory is `New World` (like default name),
 *   then you will get `New World` as world name, but if
 *   you have duplicate, for example directory `New World (2)`,
 *   but world name is `New World`, you will get `New World` as
 *   world name.
 * ```
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
val World.name: String
    get() {
        var wName = String.empty
        DistExecutor.runWhenOn(Dist.CLIENT) {
            Runnable { wName = ServerLifecycleHooks.getCurrentServer().worldName }
        }
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
            Runnable { wName = ServerLifecycleHooks.getCurrentServer().folderName }
        }
        return wName
    }

/**
 *  Returns world directory name, for server this will
 *  return server world directory name, for client, see [World.name].
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
val World.directoryName: String get() = ServerLifecycleHooks.getCurrentServer().folderName

/**
 * Returns full path as string to world directory.
 *
 * For client it `saves/<world name>`
 *
 * For server it `./<world name>`
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
val World.directoryPath: String
    get() {
        var wPath = String.empty
        DistExecutor.runWhenOn(Dist.CLIENT) {
            Runnable {
                val folderName = ServerLifecycleHooks.getCurrentServer().folderName
                wPath = "saves${File.pathSeparator}${folderName}"
            }
        }
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
            Runnable { wPath = ServerLifecycleHooks.getCurrentServer().folderName }
        }
        return wPath
    }

/**
 * Returns current player dimension type.
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
val PlayerEntity.currentDimension: DimensionType get() = this.dimension

/**
 * Returns current dimension registry name.
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
val PlayerEntity.currentDimensionName get() = this.currentDimension.registryName.toString()

/**
 * Returns current dimension id.
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
val PlayerEntity.currentDimensionId get() = this.currentDimension.id

/**
 * Returns true if living entity is player.
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
val LivingEntity.isPlayerEntity get() = this is ServerPlayerEntity

/**
 * Returns [ServerPlayerEntity] instance of player from [LivingEntity].
 *
 * @since 2.0.0-SNAPSHOT.1.
 */
val LivingEntity.asPlayerEntity get() = this as ServerPlayerEntity
