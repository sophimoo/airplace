package com.sophimoo.airplace;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;

public final class AirPlaceRenderer {
	private static final double SNAP_DISTANCE_SQ = 16.0;
	private static double lerpX;
	private static double lerpY;
	private static double lerpZ;
	private static boolean lerping;

	private AirPlaceRenderer() {
	}

	public static void init() {
		LevelRenderEvents.COLLECT_SUBMITS.register(AirPlaceRenderer::onCollectSubmits);
	}

	private static void onCollectSubmits(LevelRenderContext ctx) {
		if (AirPlaceFeature.renderPos == null) {
			lerping = false;
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return;
		}

		PoseStack poseStack = ctx.poseStack();
		SubmitNodeCollector collector = ctx.submitNodeCollector();
		Vec3 cam = ctx.levelState().cameraRenderState.pos;
		BlockPos pos = AirPlaceFeature.renderPos;
		Vec3 visual = visualPos(mc, pos);

		poseStack.pushPose();
		poseStack.translate(visual.x - cam.x, visual.y - cam.y, visual.z - cam.z);

		if (AirPlaceClient.config().ghostBlock && AirPlaceFeature.renderState != null) {
			GhostBlockRenderState moving = new GhostBlockRenderState();
			moving.randomSeedPos = pos;
			moving.blockPos = pos;
			moving.blockState = AirPlaceFeature.renderState;
			moving.biome = mc.level.getBiome(pos);
			moving.cardinalLighting = mc.level.cardinalLighting();
			moving.lightEngine = mc.level.getLightEngine();
			moving.alpha = AirPlaceClient.config().ghostOpacity / 100.0F;
			collector.submitMovingBlock(poseStack, moving, 0);
		}

		if (AirPlaceClient.config().blockOutline) {
			collector.submitShapeOutline(
				poseStack,
				AirPlaceFeature.renderState != null
					? AirPlaceFeature.renderState.getShape(mc.level, pos, CollisionContext.of(mc.player))
					: Shapes.block(),
				RenderTypes.linesTranslucent(),
				ARGB.black(102),
				ctx.gameRenderer().gameRenderState().windowRenderState.appropriateLineWidth,
				true
			);
		}

		poseStack.popPose();
	}

	private static Vec3 visualPos(Minecraft mc, BlockPos pos) {
		AirPlaceConfig cfg = AirPlaceClient.config();
		if (!cfg.ghostLerp) {
			lerping = false;
			return Vec3.atLowerCornerOf(pos);
		}

		if (!lerping) {
			lerpX = pos.getX();
			lerpY = pos.getY();
			lerpZ = pos.getZ();
			lerping = true;
			return new Vec3(lerpX, lerpY, lerpZ);
		}

		double dx = pos.getX() - lerpX;
		double dy = pos.getY() - lerpY;
		double dz = pos.getZ() - lerpZ;
		if (dx * dx + dy * dy + dz * dz > SNAP_DISTANCE_SQ) {
			lerpX = pos.getX();
			lerpY = pos.getY();
			lerpZ = pos.getZ();
			return new Vec3(lerpX, lerpY, lerpZ);
		}

		double seconds = mc.getDeltaTracker().getRealtimeDeltaTicks() / 20.0;
		double t = 1.0 - Math.exp(-cfg.ghostLerpSpeed * seconds);
		lerpX += dx * t;
		lerpY += dy * t;
		lerpZ += dz * t;
		return new Vec3(lerpX, lerpY, lerpZ);
	}
}
