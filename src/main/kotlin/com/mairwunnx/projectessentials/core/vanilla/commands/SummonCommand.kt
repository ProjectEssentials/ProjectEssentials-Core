/**
 * Command realization by Minecraft, converted
 * from java to kotlin with small changes, e.g
 * added check on permissions and aliases register.
 */

package com.mairwunnx.projectessentials.core.vanilla.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.EntryPoint
import com.mairwunnx.projectessentials.core.configuration.commands.CommandsConfigurationUtils
import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.core.vanilla.utils.NativeCommandUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandException
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.EntitySummonArgument
import net.minecraft.command.arguments.NBTCompoundTagArgument
import net.minecraft.command.arguments.SuggestionProviders
import net.minecraft.command.arguments.Vec3Argument
import net.minecraft.entity.EntityType
import net.minecraft.entity.ILivingEntityData
import net.minecraft.entity.MobEntity
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.effect.LightningBoltEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.Style
import net.minecraft.util.text.TranslationTextComponent
import org.apache.logging.log4j.LogManager

internal object SummonCommand {
    private val SUMMON_FAILED = SimpleCommandExceptionType(
        TranslationTextComponent("commands.summon.failed")
    )

    private val logger = LogManager.getLogger()
    private var aliases =
        CommandsConfigurationUtils.getConfig().aliases.summon + "summon"

    private fun tryAssignAliases() {
        if (!EntryPoint.cooldownInstalled) return
        CommandsAliases.aliases["summon"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        logger.info("Replacing `/summon` vanilla command")
        NativeCommandUtils.removeCommand("summon")
        tryAssignAliases()

        aliases.forEach { command ->
            dispatcher.register(
                Commands.literal(command).then(
                    Commands.argument(
                        "entity", EntitySummonArgument.entitySummon()
                    ).suggests(
                        SuggestionProviders.SUMMONABLE_ENTITIES
                    ).executes { p_198738_0_ ->
                        summonEntity(
                            p_198738_0_.source,
                            EntitySummonArgument.getEntityId(p_198738_0_, "entity"),
                            p_198738_0_.source.pos,
                            CompoundNBT(),
                            true
                        )
                    }.then(
                        Commands.argument(
                            "pos", Vec3Argument.vec3()
                        ).executes { p_198735_0_ ->
                            summonEntity(
                                p_198735_0_.source,
                                EntitySummonArgument.getEntityId(p_198735_0_, "entity"),
                                Vec3Argument.getVec3(p_198735_0_, "pos"),
                                CompoundNBT(),
                                true
                            )
                        }.then(
                            Commands.argument(
                                "nbt", NBTCompoundTagArgument.func_218043_a()
                            ).executes { p_198739_0_ ->
                                summonEntity(
                                    p_198739_0_.source,
                                    EntitySummonArgument.getEntityId(p_198739_0_, "entity"),
                                    Vec3Argument.getVec3(p_198739_0_, "pos"),
                                    NBTCompoundTagArgument.func_218042_a(
                                        p_198739_0_,
                                        "nbt"
                                    ),
                                    false
                                )
                            }
                        )
                    )
                )
            )
        }
    }

    private fun checkPermissions(source: CommandSource) {
        try {
            if (!EntryPoint.hasPermission(source.asPlayer(), "native.summon", 2)) {
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", source.asPlayer().name.string)
                        .replace("%1", "summon")
                )
                throw CommandException(
                    textComponentFrom(
                        source.asPlayer(),
                        LocalizationConfigurationUtils.getConfig().enabled,
                        "native.command.restricted"
                    ).setStyle(
                        Style().setHoverEvent(
                            hoverEventFrom(
                                source.asPlayer(),
                                LocalizationConfigurationUtils.getConfig().enabled,
                                "native.command.restricted_hover",
                                "native.summon", "2"
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
    private fun summonEntity(
        source: CommandSource,
        type: ResourceLocation,
        pos: Vec3d,
        nbt: CompoundNBT,
        randomizeProperties: Boolean
    ): Int {
        checkPermissions(source)

        val compoundnbt = nbt.copy()
        compoundnbt.putString("id", type.toString())
        return if (EntityType.getKey(EntityType.LIGHTNING_BOLT) == type) {
            val lightningboltentity = LightningBoltEntity(
                source.world, pos.x, pos.y, pos.z, false
            )
            source.world.addLightningBolt(lightningboltentity)
            source.sendFeedback(
                TranslationTextComponent(
                    "commands.summon.success",
                    lightningboltentity.displayName
                ), true
            )
            1
        } else {
            val serverworld = source.world
            val entity = EntityType.func_220335_a(
                compoundnbt, serverworld
            ) { p_218914_2_ ->
                p_218914_2_.setLocationAndAngles(
                    pos.x, pos.y, pos.z,
                    p_218914_2_.rotationYaw,
                    p_218914_2_.rotationPitch
                )
                if (!serverworld.summonEntity(p_218914_2_)) null else p_218914_2_
            }
            if (entity == null) {
                throw SUMMON_FAILED.create()
            } else {
                if (randomizeProperties && entity is MobEntity) {
                    entity.onInitialSpawn(
                        source.world, source.world.getDifficultyForLocation(
                            BlockPos(entity)
                        ), SpawnReason.COMMAND, null as ILivingEntityData?, null as CompoundNBT?
                    )
                }
                source.sendFeedback(
                    TranslationTextComponent(
                        "commands.summon.success",
                        entity.displayName
                    ), true
                )
                1
            }
        }
    }
}
