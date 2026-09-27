package com.sophimoo.airplace.mixin;

import com.sophimoo.airplace.GhostBlockRenderState;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.frapi.render.NonTerrainBlockRenderContext")
public class NonTerrainBlockRenderContextMixin {
	@Unique
	private GhostBlockRenderState airplace$ghost;

	@Inject(method = "tesselateBlock", at = @At("HEAD"), require = 0)
	private void airplace$captureGhost(QuadEmitter output, float x, float y, float z, BlockAndTintGetter level, BlockPos pos, BlockState blockState, BlockStateModel model, long seed, CallbackInfo ci) {
		this.airplace$ghost = level instanceof GhostBlockRenderState ghost ? ghost : null;
	}

	@Inject(method = "transform", at = @At("RETURN"), require = 0)
	private void airplace$ghostAlpha(MutableQuadView quad, CallbackInfoReturnable<Boolean> cir) {
		if (this.airplace$ghost != null) {
			quad.multiplyColor(ARGB.color((int) (this.airplace$ghost.alpha * 255.0F), 255, 255, 255));
		}
	}
}
