package com.example;

import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {
	@Override
	public void onInitialize() {
		InstantLeafDecayConfig.load();
		System.out.println("[HizliYaprak] Mod yüklendi! " +
				"DecayTicks=" + InstantLeafDecayConfig.DECAY_TICKS);
	}
}