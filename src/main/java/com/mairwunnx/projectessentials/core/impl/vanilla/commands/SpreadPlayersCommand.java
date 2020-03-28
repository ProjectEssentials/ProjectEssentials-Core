package com.mairwunnx.projectessentials.core.impl.vanilla.commands;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI;
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI;
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.Vec2Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.mairwunnx.projectessentials.core.api.v1.InternalConstantsKt.SETTING_LOC_ENABLED;
import static com.mairwunnx.projectessentials.core.api.v1.extensions.ExtensionsKt.hoverEventFrom;
import static com.mairwunnx.projectessentials.core.api.v1.extensions.ExtensionsKt.textComponentFrom;
import static com.mairwunnx.projectessentials.core.api.v1.permissions.PermissionAPIKt.hasPermission;

@SuppressWarnings("CodeBlock2Expr")
public class SpreadPlayersCommand {
    private static GeneralConfiguration generalConfiguration = null;

    private static GeneralConfiguration getGeneralConfiguration() {
        if (generalConfiguration == null) {
            generalConfiguration = ConfigurationAPI.INSTANCE.getConfigurationByName("general");
        }
        return generalConfiguration;
    }

    private static Logger logger = LogManager.getLogger();
    private static final Dynamic4CommandExceptionType SPREAD_TEAMS_FAILED = new Dynamic4CommandExceptionType((p_208910_0_, p_208910_1_, p_208910_2_, p_208910_3_) -> {
        return new TranslationTextComponent("commands.spreadplayers.failed.teams", p_208910_0_, p_208910_1_, p_208910_2_, p_208910_3_);
    });
    private static final Dynamic4CommandExceptionType SPREAD_ENTITIES_FAILED = new Dynamic4CommandExceptionType((p_208912_0_, p_208912_1_, p_208912_2_, p_208912_3_) -> {
        return new TranslationTextComponent("commands.spreadplayers.failed.entities", p_208912_0_, p_208912_1_, p_208912_2_, p_208912_3_);
    });

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        logger.info("Replacing `/spreadplayers` vanilla command");
        CommandAPI.INSTANCE.removeCommand("spreadplayers");

        dispatcher.register(Commands.literal("spreadplayers").then(Commands.argument("center", Vec2Argument.vec2()).then(Commands.argument("spreadDistance", FloatArgumentType.floatArg(0.0F)).then(Commands.argument("maxRange", FloatArgumentType.floatArg(1.0F)).then(Commands.argument("respectTeams", BoolArgumentType.bool()).then(Commands.argument("targets", EntityArgument.entities()).executes((p_198718_0_) -> {
            return spreadPlayers(p_198718_0_.getSource(), Vec2Argument.getVec2f(p_198718_0_, "center"), FloatArgumentType.getFloat(p_198718_0_, "spreadDistance"), FloatArgumentType.getFloat(p_198718_0_, "maxRange"), BoolArgumentType.getBool(p_198718_0_, "respectTeams"), EntityArgument.getEntities(p_198718_0_, "targets"));
        })))))));
    }

    private static void checkPermissions(CommandSource source) {
        try {
            if (hasPermission(
                source.asPlayer(), "native.spreadplayers", 2
            )) {
                throw new CommandException(
                    textComponentFrom(
                        source.asPlayer(),
                        getGeneralConfiguration().getBoolOrDefault(SETTING_LOC_ENABLED, false),
                        "native.command.restricted"
                    ).setStyle(
                        new Style().setHoverEvent(
                            hoverEventFrom(
                                source.asPlayer(),
                                getGeneralConfiguration().getBoolOrDefault(SETTING_LOC_ENABLED, false),
                                "native.command.restricted_hover",
                                "native.spreadplayers", "2"
                            )
                        )
                    )
                );
            }
        } catch (CommandSyntaxException e) {
            // ignored, because command executed by server.
        }
    }

    private static int spreadPlayers(CommandSource source, Vec2f center, float spreadDistance, float maxRange, boolean respectTeams, Collection<? extends Entity> targets) throws CommandSyntaxException {
        checkPermissions(source);

        Random random = new Random();
        double d0 = center.x - maxRange;
        double d1 = center.y - maxRange;
        double d2 = center.x + maxRange;
        double d3 = center.y + maxRange;
        Position[] aspreadplayerscommand$position = getPositions(random, respectTeams ? getNumberOfTeams(targets) : targets.size(), d0, d1, d2, d3);
        ensureSufficientSeparation(center, spreadDistance, source.getWorld(), random, d0, d1, d2, d3, aspreadplayerscommand$position, respectTeams);
        double d4 = doSpreading(targets, source.getWorld(), aspreadplayerscommand$position, respectTeams);
        source.sendFeedback(new TranslationTextComponent("commands.spreadplayers.success." + (respectTeams ? "teams" : "entities"), aspreadplayerscommand$position.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", d4)), true);
        return aspreadplayerscommand$position.length;
    }

    /**
     * Gets the number of unique teams for the given list of entities.
     */
    private static int getNumberOfTeams(Collection<? extends Entity> entities) {
        Set<Team> set = Sets.newHashSet();

        for (Entity entity : entities) {
            if (entity instanceof PlayerEntity) {
                set.add(entity.getTeam());
            } else {
                set.add(null);
            }
        }

        return set.size();
    }

    private static void ensureSufficientSeparation(Vec2f center, double spreadDistance, ServerWorld worldIn, Random random, double minX, double minZ, double maxX, double maxZ, Position[] positions, boolean respectTeams) throws CommandSyntaxException {
        boolean flag = true;
        double d0 = Float.MAX_VALUE;

        int i;
        for (i = 0; i < 10000 && flag; ++i) {
            flag = false;
            d0 = Float.MAX_VALUE;

            for (int j = 0; j < positions.length; ++j) {
                Position spreadplayerscommand$position = positions[j];
                int k = 0;
                Position spreadplayerscommand$position1 = new Position();

                for (int l = 0; l < positions.length; ++l) {
                    if (j != l) {
                        Position spreadplayerscommand$position2 = positions[l];
                        double d1 = spreadplayerscommand$position.getDistance(spreadplayerscommand$position2);
                        d0 = Math.min(d1, d0);
                        if (d1 < spreadDistance) {
                            ++k;
                            spreadplayerscommand$position1.x = spreadplayerscommand$position1.x + (spreadplayerscommand$position2.x - spreadplayerscommand$position.x);
                            spreadplayerscommand$position1.z = spreadplayerscommand$position1.z + (spreadplayerscommand$position2.z - spreadplayerscommand$position.z);
                        }
                    }
                }

                if (k > 0) {
                    spreadplayerscommand$position1.x = spreadplayerscommand$position1.x / (double) k;
                    spreadplayerscommand$position1.z = spreadplayerscommand$position1.z / (double) k;
                    double d2 = spreadplayerscommand$position1.getMagnitude();
                    if (d2 > 0.0D) {
                        spreadplayerscommand$position1.normalize();
                        spreadplayerscommand$position.subtract(spreadplayerscommand$position1);
                    } else {
                        spreadplayerscommand$position.computeCoords(random, minX, minZ, maxX, maxZ);
                    }

                    flag = true;
                }

                if (spreadplayerscommand$position.clampWithinRange(minX, minZ, maxX, maxZ)) {
                    flag = true;
                }
            }

            if (!flag) {
                for (Position spreadplayerscommand$position3 : positions) {
                    if (!spreadplayerscommand$position3.isLocationSafe(worldIn)) {
                        spreadplayerscommand$position3.computeCoords(random, minX, minZ, maxX, maxZ);
                        flag = true;
                    }
                }
            }
        }

        if (d0 == (double) Float.MAX_VALUE) {
            d0 = 0.0D;
        }

        if (i >= 10000) {
            if (respectTeams) {
                throw SPREAD_TEAMS_FAILED.create(positions.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", d0));
            } else {
                throw SPREAD_ENTITIES_FAILED.create(positions.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", d0));
            }
        }
    }

    /**
     * Actually spreads the target players. Positions is an array that has a length equal to the number of targets (if
     * respectTeams is false) or else the number of teams (if it is true); if respectTeams is false, then each player
     * gets its own index in positions, and if it's true, each team will get its own index into it as stored in a map.
     */
    private static double doSpreading(Collection<? extends Entity> targets, ServerWorld worldIn, Position[] positions, boolean respectTeams) {
        double d0 = 0.0D;
        int i = 0;
        Map<Team, Position> map = Maps.newHashMap();

        for (Entity entity : targets) {
            Position spreadplayerscommand$position;
            if (respectTeams) {
                Team team = entity instanceof PlayerEntity ? entity.getTeam() : null;
                if (!map.containsKey(team)) {
                    map.put(team, positions[i++]);
                }

                spreadplayerscommand$position = map.get(team);
            } else {
                spreadplayerscommand$position = positions[i++];
            }

            entity.teleportKeepLoaded((float) MathHelper.floor(spreadplayerscommand$position.x) + 0.5F, spreadplayerscommand$position.getHighestNonAirBlock(worldIn), (double) MathHelper.floor(spreadplayerscommand$position.z) + 0.5D);
            double d2 = Double.MAX_VALUE;

            for (Position spreadplayerscommand$position1 : positions) {
                if (spreadplayerscommand$position != spreadplayerscommand$position1) {
                    double d1 = spreadplayerscommand$position.getDistance(spreadplayerscommand$position1);
                    d2 = Math.min(d1, d2);
                }
            }

            d0 += d2;
        }

        if (targets.size() < 2) {
            return 0.0D;
        } else {
            d0 = d0 / (double) targets.size();
            return d0;
        }
    }

    private static Position[] getPositions(Random random, int count, double minX, double minZ, double maxX, double maxZ) {
        Position[] aspreadplayerscommand$position = new Position[count];

        for (int i = 0; i < aspreadplayerscommand$position.length; ++i) {
            Position spreadplayerscommand$position = new Position();
            spreadplayerscommand$position.computeCoords(random, minX, minZ, maxX, maxZ);
            aspreadplayerscommand$position[i] = spreadplayerscommand$position;
        }

        return aspreadplayerscommand$position;
    }

    static class Position {
        private double x;
        private double z;

        double getDistance(Position other) {
            double d0 = this.x - other.x;
            double d1 = this.z - other.z;
            return Math.sqrt(d0 * d0 + d1 * d1);
        }

        void normalize() {
            double d0 = this.getMagnitude();
            this.x /= d0;
            this.z /= d0;
        }

        float getMagnitude() {
            return MathHelper.sqrt(this.x * this.x + this.z * this.z);
        }

        public void subtract(Position other) {
            this.x -= other.x;
            this.z -= other.z;
        }

        public boolean clampWithinRange(double minX, double minZ, double maxX, double maxZ) {
            boolean flag = false;
            if (this.x < minX) {
                this.x = minX;
                flag = true;
            } else if (this.x > maxX) {
                this.x = maxX;
                flag = true;
            }

            if (this.z < minZ) {
                this.z = minZ;
                flag = true;
            } else if (this.z > maxZ) {
                this.z = maxZ;
                flag = true;
            }

            return flag;
        }

        public int getHighestNonAirBlock(IBlockReader worldIn) {
            BlockPos blockpos = new BlockPos(this.x, 256.0D, this.z);

            while (blockpos.getY() > 0) {
                blockpos = blockpos.down();
                if (!worldIn.getBlockState(blockpos).isAir()) {
                    return blockpos.getY() + 1;
                }
            }

            return 257;
        }

        /**
         * Checks if the location at the current x and z coords is safe.
         */
        public boolean isLocationSafe(IBlockReader worldIn) {
            BlockPos blockpos = new BlockPos(this.x, 256.0D, this.z);

            while (blockpos.getY() > 0) {
                blockpos = blockpos.down();
                BlockState blockstate = worldIn.getBlockState(blockpos);
                if (!blockstate.isAir()) {
                    Material material = blockstate.getMaterial();
                    return !material.isLiquid() && material != Material.FIRE;
                }
            }

            return false;
        }

        public void computeCoords(Random random, double minX, double minZ, double maxX, double maZx) {
            this.x = MathHelper.nextDouble(random, minX, maxX);
            this.z = MathHelper.nextDouble(random, minZ, maZx);
        }
    }
}
