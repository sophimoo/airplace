package com.sophimoo.airplace;

import com.sophimoo.airplace.mixin.BlockItemAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public final class AirPlaceFeature {
	public static BlockPos renderPos;
	public static BlockState renderState;

	private AirPlaceFeature() {
	}

	private record Target(BlockPos pos, BlockState state, BlockHitResult ray) {
	}

	public static void tick(Minecraft mc) {
		while (AirPlaceKeys.toggle != null && AirPlaceKeys.toggle.consumeClick()) {
			setEnabled(mc, !AirPlaceClient.config().enabled);
		}

		if (!AirPlaceClient.config().enabled) {
			clear();
			return;
		}

		Target target = computeTarget(mc);
		if (target == null) {
			clear();
			return;
		}

		renderPos = target.pos();
		renderState = target.state();
	}

	public static BlockHitResult placementHit(Minecraft mc) {
		if (!AirPlaceClient.config().enabled || mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.MISS) {
			return null;
		}
		Target target = computeTarget(mc);
		return target == null
			? null
			: new BlockHitResult(target.ray().getLocation(), target.ray().getDirection(), target.ray().getBlockPos(), false);
	}

	private static void clear() {
		renderPos = null;
		renderState = null;
	}

	private static void setEnabled(Minecraft mc, boolean enabled) {
		AirPlaceConfig cfg = AirPlaceClient.config();
		cfg.enabled = enabled;
		AirPlaceClient.saveConfig();
		if (!enabled) {
			clear();
		}
		if (mc.player != null) {
			mc.player.sendOverlayMessage(
				Component.literal("AirPlace: " + (enabled ? "ON" : "OFF")));
		}
	}

	public static void onScroll(Minecraft mc, double yoffset, CallbackInfo ci) {
		if (mc.player == null || mc.gui.screen() != null) {
			return;
		}
		if (AirPlaceKeys.scrollModifier == null || !AirPlaceKeys.scrollModifier.isDown() || yoffset == 0) {
			return;
		}

		AirPlaceConfig cfg = AirPlaceClient.config();
		cfg.range = Math.clamp(cfg.range + (yoffset > 0 ? 1 : -1), 0, 6);
		AirPlaceClient.saveConfig();
		ci.cancel();
	}

	private static Target computeTarget(Minecraft mc) {
		if (mc.player == null || mc.level == null) {
			return null;
		}

		AirPlaceConfig cfg = AirPlaceClient.config();
		HitResult ray;
		if (mc.hitResult instanceof BlockHitResult crosshair && crosshair.getType() == HitResult.Type.BLOCK) {
			ray = crosshair;
		} else {
			ray = mc.player.pick(cfg.range, 1.0F, false);
		}
		if (!(ray instanceof BlockHitResult hit)) {
			return null;
		}

		ItemStack stack = mc.player.getMainHandItem();
		if (!(stack.getItem() instanceof BlockItem blockItem)) {
			return null;
		}

		BlockPlaceContext ctx = new BlockPlaceContext(mc.player, InteractionHand.MAIN_HAND, stack, hit);
		if (!ctx.canPlace()) {
			return null;
		}

		BlockPlaceContext updatedCtx = blockItem.updatePlacementContext(ctx);
		if (updatedCtx == null) {
			return null;
		}

		BlockState state = ((BlockItemAccessor) blockItem).airplace$getPlacementState(updatedCtx);
		return state == null ? null : new Target(updatedCtx.getClickedPos(), state, hit);
	}
}
