package com.example.mixin;

import com.example.InstantLeafDecayConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeavesBlock.class)
public class FastLeafDecayMixin {

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

            if (InstantLeafDecayConfig.PARTICLES && InstantLeafDecayConfig.PARTICLE_COUNT > 0) {
                level.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, state),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        InstantLeafDecayConfig.PARTICLE_COUNT,
                        0.3, 0.3, 0.3, 0.05
                );
            }

            if (InstantLeafDecayConfig.SOUND) {
                float pitchRange = InstantLeafDecayConfig.SOUND_PITCH_MAX - InstantLeafDecayConfig.SOUND_PITCH_MIN;
                float pitch = InstantLeafDecayConfig.SOUND_PITCH_MIN + (random.nextFloat() * pitchRange);
                level.playSound(null, pos,
                        SoundEvents.GRASS_BREAK,
                        SoundSource.BLOCKS,
                        InstantLeafDecayConfig.SOUND_VOLUME,
                        pitch
                );
            }

            level.destroyBlock(pos, true);
            ci.cancel();
        }
    }
}