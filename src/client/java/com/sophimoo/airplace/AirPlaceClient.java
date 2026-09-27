package com.sophimoo.airplace;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class AirPlaceClient implements ClientModInitializer {
	private static ConfigHolder<AirPlaceConfig> holder;

	public static AirPlaceConfig config() {
		return holder.getConfig();
	}

	@Override
	public void onInitializeClient() {
		AutoConfig.register(AirPlaceConfig.class, JanksonConfigSerializer::new);
		holder = AutoConfig.getConfigHolder(AirPlaceConfig.class);

		AirPlaceKeys.register();
		AirPlaceRenderer.init();
		ClientTickEvents.END_CLIENT_TICK.register(AirPlaceFeature::tick);
	}

	public static void saveConfig() {
		holder.save();
	}
}
