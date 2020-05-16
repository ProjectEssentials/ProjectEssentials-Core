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

package com.mairwunnx.projectessentials.core.impl.vanilla.commands


import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAliases
import com.mairwunnx.projectessentials.core.api.v1.extensions.hoverEventFrom
import com.mairwunnx.projectessentials.core.api.v1.extensions.textComponentFrom
import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
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

internal object SummonCommand : VanillaCommandBase() {
    private val SUMMON_FAILED = SimpleCommandExceptionType(
        TranslationTextComponent("commands.summon.failed")
    )

    private var aliases =
        configuration.take().aliases.summon + "summon"

    private fun tryAssignAliases() {
        if (!ModuleAPI.isModuleExist("cooldown")) return
        CommandAliases.aliases["summon"] = aliases.toMutableList()
    }

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        CommandAPI.removeCommand("summon")
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
            if (!hasPermission(source.asPlayer(), "native.summon", 2)) {
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
