/*
 * ! This command implementation by Mojang Studios!
 *
 * Decompiled with idea source code was converted to kotlin code.
 * But with additions such as permissions checking and etc.
 *
 * 1. This can be bad code.
 * 2. This file can be not formatter pretty.
 */
package com.mairwunnx.projectessentials.core.impl.vanilla.commands;

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI;
import com.mairwunnx.projectessentials.core.api.v1.commands.back.BackLocationAPI;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class TeleportCommand extends VanillaCommandBase {
    public TeleportCommand() {
        super("teleport");
    }

    @Override
    public void register(@NotNull CommandDispatcher<CommandSource> dispatcher) {
        super.register(dispatcher);
        CommandAPI.INSTANCE.removeCommand("tp");
        LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register(Commands.literal("teleport").requires((p_198816_0_) -> {
            return isAllowed(p_198816_0_, "teleport", 2);
        }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("location", Vec3Argument.vec3()).executes((p_198807_0_) -> {
            return teleportToPos(p_198807_0_.getSource(), EntityArgument.getEntities(p_198807_0_, "targets"), p_198807_0_.getSource().getWorld(), Vec3Argument.getLocation(p_198807_0_, "location"), null, null);
        }).then(Commands.argument("rotation", RotationArgument.rotation()).executes((p_198811_0_) -> {
            return teleportToPos(p_198811_0_.getSource(), EntityArgument.getEntities(p_198811_0_, "targets"), p_198811_0_.getSource().getWorld(), Vec3Argument.getLocation(p_198811_0_, "location"), RotationArgument.getRotation(p_198811_0_, "rotation"), null);
        })).then(Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("facingEntity", EntityArgument.entity()).executes((p_198806_0_) -> {
            return teleportToPos(p_198806_0_.getSource(), EntityArgument.getEntities(p_198806_0_, "targets"), p_198806_0_.getSource().getWorld(), Vec3Argument.getLocation(p_198806_0_, "location"), null, new Facing(EntityArgument.getEntity(p_198806_0_, "facingEntity"), EntityAnchorArgument.Type.FEET));
        }).then(Commands.argument("facingAnchor", EntityAnchorArgument.entityAnchor()).executes((p_198812_0_) -> {
            return teleportToPos(p_198812_0_.getSource(), EntityArgument.getEntities(p_198812_0_, "targets"), p_198812_0_.getSource().getWorld(), Vec3Argument.getLocation(p_198812_0_, "location"), null, new Facing(EntityArgument.getEntity(p_198812_0_, "facingEntity"), EntityAnchorArgument.getEntityAnchor(p_198812_0_, "facingAnchor")));
        })))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes((p_198805_0_) -> {
            return teleportToPos(p_198805_0_.getSource(), EntityArgument.getEntities(p_198805_0_, "targets"), p_198805_0_.getSource().getWorld(), Vec3Argument.getLocation(p_198805_0_, "location"), null, new Facing(Vec3Argument.getVec3(p_198805_0_, "facingLocation")));
        })))).then(Commands.argument("destination", EntityArgument.entity()).executes((p_198814_0_) -> {
            return teleportToEntity(p_198814_0_.getSource(), EntityArgument.getEntities(p_198814_0_, "targets"), EntityArgument.getEntity(p_198814_0_, "destination"));
        }))).then(Commands.argument("location", Vec3Argument.vec3()).executes((p_200560_0_) -> {
            return teleportToPos(p_200560_0_.getSource(), Collections.singleton(p_200560_0_.getSource().assertIsEntity()), p_200560_0_.getSource().getWorld(), Vec3Argument.getLocation(p_200560_0_, "location"), LocationInput.current(), null);
        })).then(Commands.argument("destination", EntityArgument.entity()).executes((p_200562_0_) -> {
            return teleportToEntity(p_200562_0_.getSource(), Collections.singleton(p_200562_0_.getSource().assertIsEntity()), EntityArgument.getEntity(p_200562_0_, "destination"));
        })));
        dispatcher.register(Commands.literal("tp").requires((it) -> isAllowed(it, "teleport", 2)).redirect(literalcommandnode));
    }

    private static int teleportToEntity(CommandSource source, Collection<? extends Entity> targets, Entity destination) {
        commitBack(source);
        return net.minecraft.command.impl.TeleportCommand.teleportToEntity(source, targets, destination);
    }

    private static int teleportToPos(CommandSource source, Collection<? extends Entity> targets, ServerWorld worldIn, ILocationArgument position, @Nullable ILocationArgument rotationIn, @Nullable Facing facing) {
        commitBack(source);
        Vec3d vec3d = position.getPosition(source);
        Vec2f vec2f = rotationIn == null ? null : rotationIn.getRotation(source);
        Set<SPlayerPositionLookPacket.Flags> set = EnumSet.noneOf(SPlayerPositionLookPacket.Flags.class);
        if (position.isXRelative()) {
            set.add(SPlayerPositionLookPacket.Flags.X);
        }

        if (position.isYRelative()) {
            set.add(SPlayerPositionLookPacket.Flags.Y);
        }

        if (position.isZRelative()) {
            set.add(SPlayerPositionLookPacket.Flags.Z);
        }

        if (rotationIn == null) {
            set.add(SPlayerPositionLookPacket.Flags.X_ROT);
            set.add(SPlayerPositionLookPacket.Flags.Y_ROT);
        } else {
            if (rotationIn.isXRelative()) {
                set.add(SPlayerPositionLookPacket.Flags.X_ROT);
            }

            if (rotationIn.isYRelative()) {
                set.add(SPlayerPositionLookPacket.Flags.Y_ROT);
            }
        }

        for (Entity entity : targets) {
            if (rotationIn == null) {
                teleport(source, entity, worldIn, vec3d.x, vec3d.y, vec3d.z, set, entity.rotationYaw, entity.rotationPitch, facing);
            } else {
                teleport(source, entity, worldIn, vec3d.x, vec3d.y, vec3d.z, set, vec2f.y, vec2f.x, facing);
            }
        }

        if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.teleport.success.location.single", targets.iterator().next().getDisplayName(), vec3d.x, vec3d.y, vec3d.z), true);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.teleport.success.location.multiple", targets.size(), vec3d.x, vec3d.y, vec3d.z), true);
        }

        return targets.size();
    }

    public static void teleport(CommandSource source, Entity entityIn, ServerWorld worldIn, double x, double y, double z, Set<SPlayerPositionLookPacket.Flags> relativeList, float yaw, float pitch, @Nullable Facing facing) {
        if (entityIn instanceof ServerPlayerEntity) {
            ChunkPos chunkpos = new ChunkPos(new BlockPos(x, y, z));
            worldIn.getChunkProvider().registerTicket(TicketType.POST_TELEPORT, chunkpos, 1, entityIn.getEntityId());
            entityIn.stopRiding();
            if (((ServerPlayerEntity) entityIn).isSleeping()) {
                ((ServerPlayerEntity) entityIn).stopSleepInBed(true, true);
            }

            if (worldIn == entityIn.world) {
                ((ServerPlayerEntity) entityIn).connection.setPlayerLocation(x, y, z, yaw, pitch, relativeList);
            } else {
                ((ServerPlayerEntity) entityIn).teleport(worldIn, x, y, z, yaw, pitch);
            }

            entityIn.setRotationYawHead(yaw);
        } else {
            float f1 = MathHelper.wrapDegrees(yaw);
            float f = MathHelper.wrapDegrees(pitch);
            f = MathHelper.clamp(f, -90.0F, 90.0F);
            if (worldIn == entityIn.world) {
                entityIn.setLocationAndAngles(x, y, z, f1, f);
                entityIn.setRotationYawHead(f1);
            } else {
                entityIn.detach();
                entityIn.dimension = worldIn.dimension.getType();
                Entity entity = entityIn;
                entityIn = entityIn.getType().create(worldIn);
                if (entityIn == null) {
                    return;
                }

                entityIn.copyDataFromOld(entity);
                entityIn.setLocationAndAngles(x, y, z, f1, f);
                entityIn.setRotationYawHead(f1);
                worldIn.addFromAnotherDimension(entityIn);
            }
        }

        if (facing != null) {
            facing.updateLook(source, entityIn);
        }

        if (!(entityIn instanceof LivingEntity) || !((LivingEntity) entityIn).isElytraFlying()) {
            entityIn.setMotion(entityIn.getMotion().mul(1.0D, 0.0D, 1.0D));
            entityIn.onGround = true;
        }

    }

    static class Facing {
        private final Vec3d position;
        private final Entity entity;
        private final EntityAnchorArgument.Type anchor;

        public Facing(Entity entityIn, EntityAnchorArgument.Type anchorIn) {
            this.entity = entityIn;
            this.anchor = anchorIn;
            this.position = anchorIn.apply(entityIn);
        }

        public Facing(Vec3d positionIn) {
            this.entity = null;
            this.position = positionIn;
            this.anchor = null;
        }

        public void updateLook(CommandSource source, Entity entityIn) {
            if (this.entity != null) {
                if (entityIn instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) entityIn).lookAt(source.getEntityAnchorType(), this.entity, this.anchor);
                } else {
                    entityIn.lookAt(source.getEntityAnchorType(), this.position);
                }
            } else {
                entityIn.lookAt(source.getEntityAnchorType(), this.position);
            }
        }
    }

    private static void commitBack(CommandSource source) {
        try {
            BackLocationAPI.INSTANCE.commit(source.asPlayer());
        } catch (CommandSyntaxException e) {
            // Suppressed. Sorry.
        }
    }
}
