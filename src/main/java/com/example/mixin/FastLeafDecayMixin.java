package com.example.mixin;

import com.example.ExampleMod;
import com.example.InstantLeafDecayConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeavesBlock.class)
public class FastLeafDecayMixin {

    private static SoundEvent idc$pickSound(RandomSource random) {
        if (InstantLeafDecayConfig.SOUND_EVENTS.isEmpty()) {
            return SoundEvents.GRASS_BREAK;
        }
        String id = InstantLeafDecayConfig.SOUND_EVENTS.get(
                random.nextInt(InstantLeafDecayConfig.SOUND_EVENTS.size()));
        return idc$soundByName(id);
    }

    private static SoundEvent idc$soundByName(String id) {
        switch (id) {
            case "minecraft:block.grass.break": return SoundEvents.GRASS_BREAK;
            case "minecraft:block.azalea_leaves.break": return SoundEvents.AZALEA_LEAVES_BREAK;
            case "minecraft:block.moss.break": return SoundEvents.MOSS_BREAK;
            case "minecraft:block.azalea.break": return SoundEvents.AZALEA_BREAK;
            case "minecraft:block.flowering_azalea.break": return SoundEvents.FLOWERING_AZALEA_BREAK;
            case "minecraft:block.cherry_leaves.break": return SoundEvents.CHERRY_LEAVES_BREAK;
            case "minecraft:block.sweet_berry_bush.break": return SoundEvents.SWEET_BERRY_BUSH_BREAK;
            default: return SoundEvents.GRASS_BREAK;
        }
    }

    private static ParticleOptions idc$pickParticle(BlockState state) {
        String type = InstantLeafDecayConfig.PARTICLE_TYPE;
        if ("composter".equalsIgnoreCase(type)) {
            return ParticleTypes.COMPOSTER;
        }
        if ("happy".equalsIgnoreCase(type)) {
            return ParticleTypes.HAPPY_VILLAGER;
        }
        return new BlockParticleOption(ParticleTypes.BLOCK, state);
    }

    private static void idc$rollExtraDrops(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        Block leafBlock = state.getBlock();

        if (InstantLeafDecayConfig.EXTRA_SAPLING_CHANCE > 0.0) {
            Item sapling = idc$getSaplingFor(leafBlock);
            if (sapling != null) {
                int rolls = (int) InstantLeafDecayConfig.EXTRA_SAPLING_CHANCE;
                double remainder = InstantLeafDecayConfig.EXTRA_SAPLING_CHANCE - rolls;
                int total = rolls + (random.nextDouble() < remainder ? 1 : 0);
                for (int i = 0; i < total; i++) {
                    if (random.nextFloat() < 0.05f) {
                        idc$spawnDrop(level, pos, new ItemStack(sapling));
                    }
                }
            }
        }

        if (InstantLeafDecayConfig.EXTRA_STICK_CHANCE > 0.0) {
            int rolls = (int) InstantLeafDecayConfig.EXTRA_STICK_CHANCE;
            double remainder = InstantLeafDecayConfig.EXTRA_STICK_CHANCE - rolls;
            int total = rolls + (random.nextDouble() < remainder ? 1 : 0);
            for (int i = 0; i < total; i++) {
                if (random.nextFloat() < 0.02f) {
                    idc$spawnDrop(level, pos, new ItemStack(Items.STICK));
                }
            }
        }

        if (InstantLeafDecayConfig.EXTRA_APPLE_CHANCE > 0.0
                && (leafBlock == Blocks.OAK_LEAVES || leafBlock == Blocks.DARK_OAK_LEAVES)) {
            int rolls = (int) InstantLeafDecayConfig.EXTRA_APPLE_CHANCE;
            double remainder = InstantLeafDecayConfig.EXTRA_APPLE_CHANCE - rolls;
            int total = rolls + (random.nextDouble() < remainder ? 1 : 0);
            for (int i = 0; i < total; i++) {
                if (random.nextFloat() < 0.005f) {
                    idc$spawnDrop(level, pos, new ItemStack(Items.APPLE));
                }
            }
        }
    }

    private static Item idc$getSaplingFor(Block leaf) {
        if (leaf == Blocks.OAK_LEAVES) return Items.OAK_SAPLING;
        if (leaf == Blocks.SPRUCE_LEAVES) return Items.SPRUCE_SAPLING;
        if (leaf == Blocks.BIRCH_LEAVES) return Items.BIRCH_SAPLING;
        if (leaf == Blocks.JUNGLE_LEAVES) return Items.JUNGLE_SAPLING;
        if (leaf == Blocks.ACACIA_LEAVES) return Items.ACACIA_SAPLING;
        if (leaf == Blocks.DARK_OAK_LEAVES) return Items.DARK_OAK_SAPLING;
        if (leaf == Blocks.CHERRY_LEAVES) return Items.CHERRY_SAPLING;
        if (leaf == Blocks.MANGROVE_LEAVES) return Items.MANGROVE_PROPAGULE;
        if (leaf == Blocks.PALE_OAK_LEAVES) return Items.PALE_OAK_SAPLING;
        return null;
    }

    private static void idc$spawnDrop(ServerLevel level, BlockPos pos, ItemStack stack) {
        double x = pos.getX() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 0.5;
        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
    }

    private static boolean idc$isBlacklisted(BlockState state) {
        if (InstantLeafDecayConfig.BLACKLISTED_LEAVES.isEmpty()) return false;
        String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        return InstantLeafDecayConfig.BLACKLISTED_LEAVES.contains(id);
    }

    private static boolean idc$isDimensionDisabled(ServerLevel level) {
        if (InstantLeafDecayConfig.DISABLED_DIMENSIONS.isEmpty()) return false;
        String dim = level.dimension().toString();
        return InstantLeafDecayConfig.DISABLED_DIMENSIONS.contains(dim);
    }

    private static boolean idc$shouldSkip(BlockState state, ServerLevel level, BlockPos pos) {
        if (!InstantLeafDecayConfig.ENABLED) return true;
        if (idc$isBlacklisted(state)) return true;
        if (idc$isDimensionDisabled(level)) return true;
        if (InstantLeafDecayConfig.REQUIRE_PLAYER_NEARBY) {
            if (level.getNearestPlayer(
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    InstantLeafDecayConfig.PLAYER_RADIUS, false) == null) {
                return true;
            }
        }
        return false;
    }

    @Inject(method = "updateShape", at = @At("RETURN"))
    private void planliKir(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random,
            CallbackInfoReturnable<BlockState> cir) {

        if (!InstantLeafDecayConfig.ENABLED) return;

        BlockState newState = cir.getReturnValue();
        if (idc$isBlacklisted(newState)) return;

        if (!newState.getValue(LeavesBlock.PERSISTENT)
                && newState.getValue(LeavesBlock.DISTANCE) >= 5) {
            ticks.scheduleTick(pos, newState.getBlock(), InstantLeafDecayConfig.DECAY_TICKS);
        }
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void vanillaIptal(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random,
            CallbackInfo ci) {

        if (!InstantLeafDecayConfig.ENABLED) return;
        if (idc$isBlacklisted(state)) return;

        if (!state.getValue(LeavesBlock.PERSISTENT)
                && state.getValue(LeavesBlock.DISTANCE) >= 5) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void anindaKir(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random,
            CallbackInfo ci) {

        if (idc$shouldSkip(state, level, pos)) return;

        if (!state.getValue(LeavesBlock.PERSISTENT)
                && state.getValue(LeavesBlock.DISTANCE) >= 5) {

            // ZINCIRLEME MOD: kuyruga ekle, vanilla tick'i iptal et
            if (InstantLeafDecayConfig.CHAIN_DECAY && InstantLeafDecayConfig.CHAIN_DELAY > 0) {
                long key = pos.asLong();
                if (!ExampleMod.CHAIN_QUEUE.containsKey(key)) {
                    int extra = random.nextInt(InstantLeafDecayConfig.CHAIN_DELAY * 6 + 1);
                    long target = level.getGameTime() + InstantLeafDecayConfig.DECAY_TICKS + extra;
                    ExampleMod.CHAIN_QUEUE.put(key, target);
                }
                ci.cancel();
                return;
            }

            // NORMAL MOD: aninda curume
            if (InstantLeafDecayConfig.PARTICLES && InstantLeafDecayConfig.PARTICLE_COUNT > 0) {
                level.sendParticles(
                        idc$pickParticle(state),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        InstantLeafDecayConfig.PARTICLE_COUNT,
                        0.3, 0.3, 0.3, 0.05
                );
            }

            if (InstantLeafDecayConfig.SOUND
                    && random.nextDouble() < InstantLeafDecayConfig.SOUND_CHANCE) {
                float pitchRange = InstantLeafDecayConfig.SOUND_PITCH_MAX - InstantLeafDecayConfig.SOUND_PITCH_MIN;
                float pitch = InstantLeafDecayConfig.SOUND_PITCH_MIN + (random.nextFloat() * pitchRange);
                level.playSound(null, pos,
                        idc$pickSound(random),
                        SoundSource.BLOCKS,
                        InstantLeafDecayConfig.SOUND_VOLUME,
                        pitch
                );
            }

            idc$rollExtraDrops(level, pos, state, random);
            level.destroyBlock(pos, true);
            ci.cancel();
        }
    }
}