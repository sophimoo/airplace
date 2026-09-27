package com.sophimoo.airplace.mixin;

import com.sophimoo.airplace.AirPlaceFeature;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
	@Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
	private void airplace$onScroll(long handle, double xoffset, double yoffset, CallbackInfo ci) {
		AirPlaceFeature.onScroll(Minecraft.getInstance(), yoffset, ci);
	}
}
