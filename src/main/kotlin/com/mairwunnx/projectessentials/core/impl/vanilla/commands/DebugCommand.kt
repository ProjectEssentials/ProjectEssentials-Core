/**
 * This command implementation by Mojang.
 * And decompiled with idea source code was converted
 * to kotlin code.
 * Also added some logic, for example checking on
 * permissions, and for some commands shorten aliases.
 *
 * 1. This can be bad code.
 * 2. This file can be not formatter pretty.
 */

@file:Suppress("FunctionName")

package com.mairwunnx.projectessentials.core.impl.vanilla.commands


import com.google.common.collect.ImmutableMap
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.SharedConstants
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.spi.FileSystemProvider
import java.text.SimpleDateFormat
import java.util.*

internal object DebugCommand : VanillaCommandBase() {
    private val field_225390_a = LogManager.getLogger()
    private val NOT_RUNNING_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.debug.notRunning")
    )
    private val ALREADY_RUNNING_EXCEPTION = SimpleCommandExceptionType(
        TranslationTextComponent("commands.debug.alreadyRunning")
    )
    private val field_225391_d =
        FileSystemProvider.installedProviders().stream().filter { p_225386_0_ ->
            p_225386_0_.scheme.equals("jar", ignoreCase = true)
        }.findFirst().orElse(null as FileSystemProvider?)

    private var aliases =
        configuration.take().aliases.debug + "debug"

    private fun tryAssignAliases() {
        CommandAliases.aliases["debug"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("debug")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.literal("start").executes { p_198329_0_ ->
                        startDebug(p_198329_0_.source)
                    }
                ).then(
                    Commands.literal("stop").executes { p_198333_0_ ->
                        stopDebug(p_198333_0_.source)
                    }
                ).then(
                    Commands.literal("report").executes { p_225388_0_ ->
                        func_225389_c(p_225388_0_.source)
                    }
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!hasPermission(source.asPlayer(), "native.debug", 3)) {
                throw CommandException(
                    textComponentFrom(
                        source.asPlayer(),
                        generalConfiguration.getBool(SETTING_LOC_ENABLED),
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            hoverEventFrom(
                                source.asPlayer(),
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                "native.command.restricted_hover",
                                "native.debug", "3"
                            )
                        )
                    )
                )
            }
        } catch (e: CommandSyntaxException) {
            // ignored, because command executed by server.
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun startDebug(source: CommandSource): Int {
        checkPermissions(source)

        val minecraftserver = source.server
        val debugprofiler = minecraftserver.profiler
        return if (debugprofiler.func_219899_d().isEnabled) {
            throw ALREADY_RUNNING_EXCEPTION.create()
        } else {
            minecraftserver.enableProfiling()
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.debug.started",
                    "Started the debug profiler. Type '/debug stop' to stop it."
                ), true
            )
            0
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun stopDebug(source: CommandSource): Int {
        checkPermissions(source)

        val minecraftserver = source.server
        val debugprofiler = minecraftserver.profiler
        return if (!debugprofiler.func_219899_d().isEnabled) {
            throw NOT_RUNNING_EXCEPTION.create()
        } else {
            val iprofileresult = debugprofiler.func_219899_d().func_219938_b()
            val file1 = File(
                minecraftserver.getFile("debug"),
                "profile-results-" + SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(Date()) + ".txt"
            )
            iprofileresult.writeToFile(file1)
            val f = iprofileresult.nanoTime().toFloat() / 1.0E9f
            val f1 = iprofileresult.ticksSpend().toFloat() / f
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.debug.stopped", String.format(
                        Locale.ROOT, "%.2f", f
                    ), iprofileresult.ticksSpend(), String.format("%.2f", f1)
                ), true
            )
            MathHelper.floor(f1)
        }
    }

    private fun func_225389_c(p_225389_0_: CommandSource): Int {
        checkPermissions(p_225389_0_)

        val minecraftserver = p_225389_0_.server
        val s = "debug-report-" + SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(Date())
        return try {
            val path1 = minecraftserver.getFile("debug").toPath()
            Files.createDirectories(path1)
            if (!SharedConstants.developmentMode && field_225391_d != null) {
                val path2 = path1.resolve("$s.zip")
                field_225391_d.newFileSystem(
                    path2,
                    ImmutableMap.of<String, String?>("create", "true")
                ).use { filesystem ->
                    minecraftserver.func_223711_a(filesystem.getPath("/"))
                }
            } else {
                val path = path1.resolve(s)
                minecraftserver.func_223711_a(path)
            }
            p_225389_0_.sendFeedback(
                TranslationTextComponent("commands.debug.reportSaved", s),
                false
            )
            1
        } catch (ioexception: IOException) {
            field_225390_a.error("Failed to save debug dump", ioexception as Throwable)
            p_225389_0_.sendErrorMessage(
                TranslationTextComponent("commands.debug.reportFailed")
            )
            0
        }
    }
}

