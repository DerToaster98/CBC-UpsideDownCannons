package rbasamoyai.createbigcannons.crafting.casting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.index.CBCBlockPartials;

import java.util.List;
import java.util.Random;

public abstract class AbstractCannonCastBlockEntityRenderer extends SafeTileEntityRenderer<AbstractCannonCastBlockEntity> {

	private final BlockRenderDispatcher dispatcher;
	
	protected AbstractCannonCastBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.dispatcher = context.getBlockRenderDispatcher();
	}
	
	@Override
	protected void renderSafe(AbstractCannonCastBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		if (!te.canRenderCastModel()) return;
		BlockState state = te.getBlockState();
		
		ms.pushPose();

		CannonCastShape shape = te.getRenderedSize();
		if (shape != null) {
			CachedBufferer.partial(CBCBlockPartials.cannonCastFor(shape), state)
					.light(light)
					.renderInto(ms, buffer.getBuffer(RenderType.solid()));
		}
		
		if (te.isController()) {
			LerpedFloat levelLerped = te.getFluidLevel();
			if (levelLerped != null && shape != null) {
				float level = levelLerped.getValue(partialTicks);

				boolean flag = shape.isLarge();
				float width = flag ? 2.875f : 0.875f;
				float height = ((float) te.height - 0.125f) * level;
				
				if (height > 0.0625f) {
					ms.pushPose();
					float f = flag ? -15 / 16f : 1 / 16f;
					ms.translate(f, 0.0625f, f);
					this.renderFluidBox(te, width, height, buffer, ms, light);
					ms.popPose();
				}
			}
			
			LerpedFloat castProgressLerped = te.getCastingLevel();
			if (castProgressLerped != null) {
				float alpha = castProgressLerped.getValue(partialTicks);			
				for (int l = 0; l < te.resultPreview.size(); ++l) {
					BlockState state1 = te.resultPreview.get(l);
					VertexConsumer vCons = buffer.getBuffer(Sheets.translucentItemSheet());
					BakedModel model = this.dispatcher.getBlockModel(state1);
					Random rand = new Random();
					ms.pushPose();
					ms.translate(0, l, 0);
					
					for (Direction dir : Direction.values()) {
						rand.setSeed(42L);
						renderQuadList(ms.last(), vCons, 1f, 1f, 1f, alpha, model.getQuads(state, dir, rand), light, overlay);
					}
					
					rand.setSeed(42L);
					renderQuadList(ms.last(), vCons, 1f, 1f, 1f, alpha, model.getQuads(state, null, rand), light, overlay);
					
					ms.popPose();
				}
			}
		}
		
		ms.popPose();
	}

	protected abstract void renderFluidBox(AbstractCannonCastBlockEntity cast, float width, float height, MultiBufferSource buffers, PoseStack stack, int light);
	
	// Taken from GhostBlockRenderer.TransparentGhostBlockRenderer
	private static void renderQuadList(PoseStack.Pose pose, VertexConsumer consumer, float red, float green, float blue, float alpha, List<BakedQuad> quads, int packedLight, int packedOverlay) {
		for (BakedQuad quad : quads) {
			float f;
			float f1;
			float f2;
			if (quad.isTinted()) {
				f = Mth.clamp(red, 0.0F, 1.0F);
				f1 = Mth.clamp(green, 0.0F, 1.0F);
				f2 = Mth.clamp(blue, 0.0F, 1.0F);
			} else {
				f = 1.0F;
				f1 = 1.0F;
				f2 = 1.0F;
			}

			consumer.putBulkData(pose, quad, f, f1, f2, packedLight, packedOverlay);
		}

	}

}