package com.example.mixin;

import com.example.InstantLeafDecayConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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

        BlockState newState = cir.getReturnValue();
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

        if (!state.getValue(LeavesBlock.PERSISTENT)
                && state.getValue(LeavesBlock.DISTANCE) >= 5) {

            if (InstantLeafDecayConfig.PARTICLES) {
                level.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, state),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        8, 0.3, 0.3, 0.3, 0.05
                );
            }

            if (InstantLeafDecayConfig.SOUND) {
                level.playSound(null, pos,
                        SoundEvents.GRASS_BREAK,
                        SoundSource.BLOCKS,
                        0.8f, 1.0f + (random.nextFloat() * 0.2f)
                );
            }

            level.destroyBlock(pos, true);
            ci.cancel();
        }
    }
}