package com.cukkoo.instantleafdecay;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class InstantLeafDecayMain implements ModInitializer {

	public static final Map<Long, Long> CHAIN_QUEUE = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		InstantLeafDecayConfig.load();
		System.out.println("[HizliYaprak] Mod yuklendi!");

		// KOMUT SISTEMI KAYDI
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			// NOT: Komutlar su an herkese acik. Multiplayer sunucuda LuckPerms gibi
			// permission modu ile /leafdecay komutunu kisitlayabilirsiniz.
			dispatcher.register(literal("leafdecay")
					.requires(source -> {
						try {
							// Reflection ile permissionLevel'e eris
							java.lang.reflect.Field field = source.getClass().getDeclaredField("permissionLevel");
							field.setAccessible(true);
							return (int) field.get(source) >= 2;
						} catch (Exception e) {
							return true; // Hata olursa herkese izin ver
						}
					})
					// 2. TUM AYARLAR ICIN SET KOMUTLARI
					.then(literal("set")
							// --- BOOLEANS (true/false) ---
							.then(literal("enabled").then(argument("value", BoolArgumentType.bool()).executes(context -> {
								boolean val = BoolArgumentType.getBool(context, "value");
								InstantLeafDecayConfig.ENABLED = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] enabled ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
							.then(literal("particles").then(argument("value", BoolArgumentType.bool()).executes(context -> {
								boolean val = BoolArgumentType.getBool(context, "value");
								InstantLeafDecayConfig.PARTICLES = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] particles ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
							.then(literal("sound").then(argument("value", BoolArgumentType.bool()).executes(context -> {
								boolean val = BoolArgumentType.getBool(context, "value");
								InstantLeafDecayConfig.SOUND = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] sound ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
							.then(literal("chainDecay").then(argument("value", BoolArgumentType.bool()).executes(context -> {
								boolean val = BoolArgumentType.getBool(context, "value");
								InstantLeafDecayConfig.CHAIN_DECAY = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] chainDecay ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
							.then(literal("requirePlayerNearby").then(argument("value", BoolArgumentType.bool()).executes(context -> {
								boolean val = BoolArgumentType.getBool(context, "value");
								InstantLeafDecayConfig.REQUIRE_PLAYER_NEARBY = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] requirePlayerNearby ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))

							// --- INTEGERS (Tam Sayılar) ---
							.then(literal("decayTicks").then(argument("value", IntegerArgumentType.integer(0)).executes(context -> {
								int val = IntegerArgumentType.getInteger(context, "value");
								InstantLeafDecayConfig.DECAY_TICKS = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] decayTicks ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
							.then(literal("particleCount").then(argument("value", IntegerArgumentType.integer(0)).executes(context -> {
								int val = IntegerArgumentType.getInteger(context, "value");
								InstantLeafDecayConfig.PARTICLE_COUNT = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] particleCount ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
							.then(literal("chainDelay").then(argument("value", IntegerArgumentType.integer(0)).executes(context -> {
								int val = IntegerArgumentType.getInteger(context, "value");
								InstantLeafDecayConfig.CHAIN_DELAY = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] chainDelay ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))

							// --- DOUBLES (Ondalık Sayılar) ---
							.then(literal("soundChance").then(argument("value", DoubleArgumentType.doubleArg(0.0, 1.0)).executes(context -> {
								double val = DoubleArgumentType.getDouble(context, "value");
								InstantLeafDecayConfig.SOUND_CHANCE = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] soundChance ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
							.then(literal("extraSaplingChance").then(argument("value", DoubleArgumentType.doubleArg(0.0)).executes(context -> {
								double val = DoubleArgumentType.getDouble(context, "value");
								InstantLeafDecayConfig.EXTRA_SAPLING_CHANCE = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] extraSaplingChance ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
							.then(literal("extraStickChance").then(argument("value", DoubleArgumentType.doubleArg(0.0)).executes(context -> {
								double val = DoubleArgumentType.getDouble(context, "value");
								InstantLeafDecayConfig.EXTRA_STICK_CHANCE = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] extraStickChance ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
							.then(literal("extraAppleChance").then(argument("value", DoubleArgumentType.doubleArg(0.0)).executes(context -> {
								double val = DoubleArgumentType.getDouble(context, "value");
								InstantLeafDecayConfig.EXTRA_APPLE_CHANCE = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] extraAppleChance ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
							.then(literal("playerRadius").then(argument("value", DoubleArgumentType.doubleArg(1.0)).executes(context -> {
								double val = DoubleArgumentType.getDouble(context, "value");
								InstantLeafDecayConfig.PLAYER_RADIUS = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] playerRadius ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))

							// --- FLOATS (Hassas Ondalıklar) ---
							.then(literal("soundVolume").then(argument("value", FloatArgumentType.floatArg(0.0f, 1.0f)).executes(context -> {
								float val = FloatArgumentType.getFloat(context, "value");
								InstantLeafDecayConfig.SOUND_VOLUME = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] soundVolume ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
							.then(literal("soundPitchMin").then(argument("value", FloatArgumentType.floatArg(0.0f, 2.0f)).executes(context -> {
								float val = FloatArgumentType.getFloat(context, "value");
								InstantLeafDecayConfig.SOUND_PITCH_MIN = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] soundPitchMin ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
							.then(literal("soundPitchMax").then(argument("value", FloatArgumentType.floatArg(0.0f, 2.0f)).executes(context -> {
								float val = FloatArgumentType.getFloat(context, "value");
								InstantLeafDecayConfig.SOUND_PITCH_MAX = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] soundPitchMax ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))

							// --- STRINGS (Yazılar) ---
							.then(literal("particleType").then(argument("value", StringArgumentType.word()).executes(context -> {
								String val = StringArgumentType.getString(context, "value");
								InstantLeafDecayConfig.PARTICLE_TYPE = val;
								InstantLeafDecayConfig.save();
								context.getSource().sendSuccess(() -> Component.literal("§e[HizliYaprak] particleType ayari §b" + val + " §eolarak guncellendi!"), true);
								return 1;
							})))
					)
			);
		});

		// TICK KONTROLU (Zincirleme Çürüme İçin)
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