package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExampleMod implements ModInitializer {

	// pos.asLong() -> target game tick
	public static final Map<Long, Long> CHAIN_QUEUE = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		InstantLeafDecayConfig.load();
		System.out.println("[HizliYaprak] Mod yuklendi!");

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (CHAIN_QUEUE.isEmpty()) return;

			for (ServerLevel level : server.getAllLevels()) {
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

					// Partikul
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

					// Ses (sansli ise)
					if (InstantLeafDecayConfig.SOUND
							&& level.getRandom().nextDouble() < InstantLeafDecayConfig.SOUND_CHANCE) {
						float pitchRange = InstantLeafDecayConfig.SOUND_PITCH_MAX - InstantLeafDecayConfig.SOUND_PITCH_MIN;
						float pitch = InstantLeafDecayConfig.SOUND_PITCH_MIN + (level.getRandom().nextFloat() * pitchRange);

						SoundEvent sound = SoundEvents.GRASS_BREAK;
						if (!InstantLeafDecayConfig.SOUND_EVENTS.isEmpty()) {
							String id = InstantLeafDecayConfig.SOUND_EVENTS.get(
									level.getRandom().nextInt(InstantLeafDecayConfig.SOUND_EVENTS.size()));
							switch (id) {
								case "minecraft:block.grass.break": sound = SoundEvents.GRASS_BREAK; break;
								case "minecraft:block.azalea_leaves.break": sound = SoundEvents.AZALEA_LEAVES_BREAK; break;
								case "minecraft:block.moss.break": sound = SoundEvents.MOSS_BREAK; break;
								case "minecraft:block.azalea.break": sound = SoundEvents.AZALEA_BREAK; break;
								case "minecraft:block.flowering_azalea.break": sound = SoundEvents.FLOWERING_AZALEA_BREAK; break;
								case "minecraft:block.cherry_leaves.break": sound = SoundEvents.CHERRY_LEAVES_BREAK; break;
								case "minecraft:block.sweet_berry_bush.break": sound = SoundEvents.SWEET_BERRY_BUSH_BREAK; break;
							}
						}

						level.playSound(null, pos, sound, SoundSource.BLOCKS,
								InstantLeafDecayConfig.SOUND_VOLUME, pitch);
					}

					level.destroyBlock(pos, true);
					it.remove();
				}
			}
		});
	}
}