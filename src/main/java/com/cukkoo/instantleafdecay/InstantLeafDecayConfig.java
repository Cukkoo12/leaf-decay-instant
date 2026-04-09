package com.cukkoo.instantleafdecay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InstantLeafDecayConfig {

    // Ana aç/kapa
    public static boolean ENABLED = true;

    // Çürüme
    public static int DECAY_TICKS = 2;

    // Partikül
    public static boolean PARTICLES = true;
    public static int PARTICLE_COUNT = 8;
    public static String PARTICLE_TYPE = "block"; // "block" = yaprak texture, "composter" = yesil yildizlar

    // Ses
    public static boolean SOUND = true;
    public static float SOUND_VOLUME = 0.8f;
    public static float SOUND_PITCH_MIN = 1.0f;
    public static float SOUND_PITCH_MAX = 1.2f;
    public static double SOUND_CHANCE = 0.3;
    public static List<String> SOUND_EVENTS = new ArrayList<>(java.util.Arrays.asList(
            "minecraft:block.grass.break",
            "minecraft:block.azalea_leaves.break",
            "minecraft:block.moss.break"
    ));

    // Filtreler
    public static List<String> BLACKLISTED_LEAVES = new ArrayList<>();
    public static List<String> DISABLED_DIMENSIONS = new ArrayList<>();
    // Ekstra droplar (vanilla uzerine)
    public static double EXTRA_SAPLING_CHANCE = 0.0;
    public static double EXTRA_STICK_CHANCE = 0.0;
    public static double EXTRA_APPLE_CHANCE = 0.0;
    // Zincirleme curume
    public static boolean CHAIN_DECAY = false;
    public static int CHAIN_DELAY = 2;
    // Performans
    public static boolean REQUIRE_PLAYER_NEARBY = false;
    public static double PLAYER_RADIUS = 64.0;

    private static final Path CONFIG_PATH = Paths.get("config/instant_leaf_decay.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                Reader reader = Files.newBufferedReader(CONFIG_PATH);
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                reader.close();

                if (data != null) {
                    ENABLED = data.enabled;
                    DECAY_TICKS = Math.max(1, data.decayTicks);
                    PARTICLES = data.particles;
                    PARTICLE_COUNT = Math.max(0, data.particleCount);
                    PARTICLE_TYPE = data.particleType != null ? data.particleType : "block";
                    SOUND = data.sound;
                    SOUND_VOLUME = clamp(data.soundVolume, 0.0f, 1.0f);
                    SOUND_PITCH_MIN = Math.max(0.5f, data.soundPitchMin);
                    SOUND_PITCH_MAX = Math.max(SOUND_PITCH_MIN, data.soundPitchMax);
                    SOUND_CHANCE = Math.max(0.0, Math.min(1.0, data.soundChance));
                    if (data.soundEvents != null && !data.soundEvents.isEmpty()) {
                        SOUND_EVENTS = data.soundEvents;
                    }
                    BLACKLISTED_LEAVES = data.blacklistedLeaves != null ? data.blacklistedLeaves : new ArrayList<>();
                    DISABLED_DIMENSIONS = data.disabledDimensions != null ? data.disabledDimensions : new ArrayList<>();
                    EXTRA_SAPLING_CHANCE = Math.max(0.0, data.extraSaplingChance);
                    EXTRA_STICK_CHANCE = Math.max(0.0, data.extraStickChance);
                    EXTRA_APPLE_CHANCE = Math.max(0.0, data.extraAppleChance);
                    CHAIN_DECAY = data.chainDecay;
                    CHAIN_DELAY = Math.max(0, data.chainDelay);
                    REQUIRE_PLAYER_NEARBY = data.requirePlayerNearby;
                    PLAYER_RADIUS = Math.max(1.0, data.playerRadius);

                }
                // Eksik alanları doldurmak için tekrar kaydet
                save();
            } else {
                save();
            }
        } catch (Exception e) {
            System.err.println("[HizliYaprak] Config yuklenemedi: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Writer writer = Files.newBufferedWriter(CONFIG_PATH);
            GSON.toJson(new ConfigData(), writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("[HizliYaprak] Config kaydedilemedi: " + e.getMessage());
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static class ConfigData {
        boolean enabled = ENABLED;
        int decayTicks = DECAY_TICKS;
        boolean particles = PARTICLES;
        int particleCount = PARTICLE_COUNT;
        boolean sound = SOUND;
        float soundVolume = SOUND_VOLUME;
        float soundPitchMin = SOUND_PITCH_MIN;
        float soundPitchMax = SOUND_PITCH_MAX;
        List<String> blacklistedLeaves = BLACKLISTED_LEAVES;
        List<String> disabledDimensions = DISABLED_DIMENSIONS;
        double extraSaplingChance = EXTRA_SAPLING_CHANCE;
        double extraStickChance = EXTRA_STICK_CHANCE;
        double extraAppleChance = EXTRA_APPLE_CHANCE;
        boolean chainDecay = CHAIN_DECAY;
        int chainDelay = CHAIN_DELAY;
        boolean requirePlayerNearby = REQUIRE_PLAYER_NEARBY;
        double playerRadius = PLAYER_RADIUS;
        double soundChance = SOUND_CHANCE;
        List<String> soundEvents = SOUND_EVENTS;
        String particleType = PARTICLE_TYPE;
    }
}