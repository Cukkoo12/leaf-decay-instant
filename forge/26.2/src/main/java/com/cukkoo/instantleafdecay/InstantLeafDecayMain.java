package com.cukkoo.instantleafdecay;

import com.mojang.brigadier.arguments.*;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod("instant_leaf_decay")
public class InstantLeafDecayMain {

    public static final Map<Long, Long> CHAIN_QUEUE = new ConcurrentHashMap<>();

    public InstantLeafDecayMain() {
        InstantLeafDecayConfig.load();
        System.out.println("[HizliYaprak] Mod yuklendi!");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        InstantLeafDecayConfig.load();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        CHAIN_QUEUE.clear();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("leafdecay")
                .then(Commands.literal("set")
                        .then(Commands.literal("enabled").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> {
                            boolean val = BoolArgumentType.getBool(ctx, "value");
                            InstantLeafDecayConfig.ENABLED = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] enabled ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("particles").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> {
                            boolean val = BoolArgumentType.getBool(ctx, "value");
                            InstantLeafDecayConfig.PARTICLES = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] particles ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("sound").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> {
                            boolean val = BoolArgumentType.getBool(ctx, "value");
                            InstantLeafDecayConfig.SOUND = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] sound ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("chainDecay").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> {
                            boolean val = BoolArgumentType.getBool(ctx, "value");
                            InstantLeafDecayConfig.CHAIN_DECAY = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] chainDecay ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("requirePlayerNearby").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> {
                            boolean val = BoolArgumentType.getBool(ctx, "value");
                            InstantLeafDecayConfig.REQUIRE_PLAYER_NEARBY = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] requirePlayerNearby ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("decayTicks").then(Commands.argument("value", IntegerArgumentType.integer(0)).executes(ctx -> {
                            int val = IntegerArgumentType.getInteger(ctx, "value");
                            InstantLeafDecayConfig.DECAY_TICKS = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] decayTicks ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("particleCount").then(Commands.argument("value", IntegerArgumentType.integer(0)).executes(ctx -> {
                            int val = IntegerArgumentType.getInteger(ctx, "value");
                            InstantLeafDecayConfig.PARTICLE_COUNT = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] particleCount ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("chainDelay").then(Commands.argument("value", IntegerArgumentType.integer(0)).executes(ctx -> {
                            int val = IntegerArgumentType.getInteger(ctx, "value");
                            InstantLeafDecayConfig.CHAIN_DELAY = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] chainDelay ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("soundChance").then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0)).executes(ctx -> {
                            double val = DoubleArgumentType.getDouble(ctx, "value");
                            InstantLeafDecayConfig.SOUND_CHANCE = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] soundChance ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("extraSaplingChance").then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0)).executes(ctx -> {
                            double val = DoubleArgumentType.getDouble(ctx, "value");
                            InstantLeafDecayConfig.EXTRA_SAPLING_CHANCE = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] extraSaplingChance ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("extraStickChance").then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0)).executes(ctx -> {
                            double val = DoubleArgumentType.getDouble(ctx, "value");
                            InstantLeafDecayConfig.EXTRA_STICK_CHANCE = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] extraStickChance ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("extraAppleChance").then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0)).executes(ctx -> {
                            double val = DoubleArgumentType.getDouble(ctx, "value");
                            InstantLeafDecayConfig.EXTRA_APPLE_CHANCE = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] extraAppleChance ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("playerRadius").then(Commands.argument("value", DoubleArgumentType.doubleArg(1.0)).executes(ctx -> {
                            double val = DoubleArgumentType.getDouble(ctx, "value");
                            InstantLeafDecayConfig.PLAYER_RADIUS = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] playerRadius ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("soundVolume").then(Commands.argument("value", FloatArgumentType.floatArg(0.0f, 1.0f)).executes(ctx -> {
                            float val = FloatArgumentType.getFloat(ctx, "value");
                            InstantLeafDecayConfig.SOUND_VOLUME = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] soundVolume ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("soundPitchMin").then(Commands.argument("value", FloatArgumentType.floatArg(0.0f, 2.0f)).executes(ctx -> {
                            float val = FloatArgumentType.getFloat(ctx, "value");
                            InstantLeafDecayConfig.SOUND_PITCH_MIN = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] soundPitchMin ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("soundPitchMax").then(Commands.argument("value", FloatArgumentType.floatArg(0.0f, 2.0f)).executes(ctx -> {
                            float val = FloatArgumentType.getFloat(ctx, "value");
                            InstantLeafDecayConfig.SOUND_PITCH_MAX = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] soundPitchMax ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                        .then(Commands.literal("particleType").then(Commands.argument("value", StringArgumentType.word()).executes(ctx -> {
                            String val = StringArgumentType.getString(ctx, "value");
                            InstantLeafDecayConfig.PARTICLE_TYPE = val;
                            InstantLeafDecayConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] particleType ayari §b" + val + " §eolarak guncellendi!"), true);
                            return 1;
                        })))
                )
        );
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (CHAIN_QUEUE.isEmpty()) return;

        var server = event.server();
        if (server == null) return;

        for (var level : server.getAllLevels()) {
            long now = level.getGameTime();
            Iterator<Map.Entry<Long, Long>> it = CHAIN_QUEUE.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, Long> queueEntry = it.next();
                if (now < queueEntry.getValue()) continue;

                BlockPos pos = BlockPos.of(queueEntry.getKey());
                BlockState state = level.getBlockState(pos);

                if (!(state.getBlock() instanceof LeavesBlock)) {
                    it.remove();
                    continue;
                }

                if (InstantLeafDecayConfig.PARTICLES && InstantLeafDecayConfig.PARTICLE_COUNT > 0) {
                    ParticleOptions particle;
                    String type = InstantLeafDecayConfig.PARTICLE_TYPE;
                    if ("composter".equalsIgnoreCase(type)) {
                        particle = ParticleTypes.COMPOSTER;
                    } else if ("happy".equalsIgnoreCase(type)) {
                        particle = ParticleTypes.HAPPY_VILLAGER;
                    } else {
                        particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
                    }
                    level.sendParticles(
                            particle,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            InstantLeafDecayConfig.PARTICLE_COUNT,
                            0.3, 0.3, 0.3, 0.05
                    );
                }

                if (InstantLeafDecayConfig.SOUND
                        && level.getRandom().nextDouble() < InstantLeafDecayConfig.SOUND_CHANCE) {
                    float pitchRange = InstantLeafDecayConfig.SOUND_PITCH_MAX - InstantLeafDecayConfig.SOUND_PITCH_MIN;
                    float pitch = InstantLeafDecayConfig.SOUND_PITCH_MIN + (level.getRandom().nextFloat() * pitchRange);

                    SoundEvent sound = SoundEvents.GRASS_BREAK;
                    if (!InstantLeafDecayConfig.SOUND_EVENTS.isEmpty()) {
                        String id = InstantLeafDecayConfig.SOUND_EVENTS.get(
                                level.getRandom().nextInt(InstantLeafDecayConfig.SOUND_EVENTS.size()));
                        sound = switch (id) {
                            case "minecraft:block.grass.break" -> SoundEvents.GRASS_BREAK;
                            case "minecraft:block.azalea_leaves.break" -> SoundEvents.AZALEA_LEAVES_BREAK;
                            case "minecraft:block.moss.break" -> SoundEvents.MOSS_BREAK;
                            case "minecraft:block.azalea.break" -> SoundEvents.AZALEA_BREAK;
                            case "minecraft:block.flowering_azalea.break" -> SoundEvents.FLOWERING_AZALEA_BREAK;
                            case "minecraft:block.cherry_leaves.break" -> SoundEvents.CHERRY_LEAVES_BREAK;
                            case "minecraft:block.sweet_berry_bush.break" -> SoundEvents.SWEET_BERRY_BUSH_BREAK;
                            default -> SoundEvents.GRASS_BREAK;
                        };
                    }

                    level.playSound(null, pos, sound, SoundSource.BLOCKS,
                            InstantLeafDecayConfig.SOUND_VOLUME, pitch);
                }

                level.destroyBlock(pos, true);
                it.remove();
            }
        }
    }
}
