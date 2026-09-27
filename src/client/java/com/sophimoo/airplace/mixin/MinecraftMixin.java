package com.sophimoo.airplace.mixin;

import com.sophimoo.airplace.AirPlaceFeature;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@WrapMethod(method = "startUseItem")
	private void airplace$useAirTarget(Operation<Void> original) {
		Minecraft mc = (Minecraft) (Object) this;
		HitResult originalHit = mc.hitResult;
		BlockHitResult placementHit = AirPlaceFeature.placementHit(mc);
		if (placementHit != null) {
			mc.hitResult = placementHit;
		}

		try {
			original.call();
		} finally {
			mc.hitResult = originalHit;
		}
	}
}
