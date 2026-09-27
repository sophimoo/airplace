package com.sophimoo.airplace;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class AirPlaceKeys {
	private static final int KEY_LEFT_ALT = 226;

	public static final KeyMapping.Category CATEGORY =
		KeyMapping.Category.register(Identifier.fromNamespaceAndPath("airplace", "main"));

	public static KeyMapping scrollModifier;
	public static KeyMapping toggle;

	private AirPlaceKeys() {
	}

	public static void register() {
		scrollModifier = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.airplace.scrollmodifier", KEY_LEFT_ALT, CATEGORY));
		toggle = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.airplace.toggle", InputConstants.UNKNOWN.getValue(), CATEGORY));
	}
}
