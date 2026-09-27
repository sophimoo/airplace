package com.sophimoo.airplace.mixin;

import com.sophimoo.airplace.GhostBlockRenderState;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SubmitNodeCollection.class)
public class SubmitNodeCollectionMixin {
	@ModifyExpressionValue(
		method = "submitMovingBlock",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;hasMaterialFlag(I)Z")
	)
	private boolean airplace$forceTranslucent(boolean original, PoseStack poseStack, MovingBlockRenderState state, int outlineColor) {
		return original || state instanceof GhostBlockRenderState;
	}

	@Redirect(
		method = "submitMovingBlock",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/renderer/SubmitNodeCollection;translucentBlocksAndItems:Lnet/minecraft/client/renderer/feature/phase/FeatureRenderPhase;",
			opcode = Opcodes.GETFIELD
		)
	)
	private FeatureRenderPhase<? super TranslucentSubmit> airplace$ghostAfterTerrain(
		SubmitNodeCollection collection, PoseStack poseStack, MovingBlockRenderState state, int outlineColor
	) {
		return state instanceof GhostBlockRenderState ? collection.afterTerrain : collection.translucentBlocksAndItems;
	}
}
