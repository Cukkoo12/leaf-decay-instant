package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
					Map.Entry<Long, Long> entry = it.next();
					if (now < entry.getValue()) continue;

					BlockPos pos = BlockPos.of(entry.getKey());
					BlockState state = level.getBlockState(pos);

					if (!(state.getBlock() instanceof LeavesBlock)) {
						it.remove();
						continue;
					}

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
						float pitch = InstantLeafDecayConfig.SOUND_PITCH_MIN + (level.getRandom().nextFloat() * pitchRange);
						level.playSound(null, pos,
								SoundEvents.GRASS_BREAK,
								SoundSource.BLOCKS,
								InstantLeafDecayConfig.SOUND_VOLUME,
								pitch
						);
					}

					level.destroyBlock(pos, true);
					it.remove();
				}
			}
		});
	}
}