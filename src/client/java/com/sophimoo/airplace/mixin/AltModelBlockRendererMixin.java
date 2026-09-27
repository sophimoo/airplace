package com.sophimoo.airplace.mixin;

import com.sophimoo.airplace.GhostBlockRenderState;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AltModelBlockRendererImpl;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AltModelBlockRendererImpl.class)
public class AltModelBlockRendererMixin {
	@Shadow
	private net.minecraft.client.renderer.block.BlockAndTintGetter level;

	@Inject(method = "transform", at = @At("RETURN"))
	private void airplace$ghostAlpha(MutableQuadView quad, CallbackInfoReturnable<Boolean> cir) {
		if (this.level instanceof GhostBlockRenderState ghost) {
			quad.multiplyColor(ARGB.color((int) (ghost.alpha * 255.0F), 255, 255, 255));
		}
	}
}
