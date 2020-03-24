package com.github.commoble.magus.client;

import com.github.commoble.magus.Magus;
import com.github.commoble.magus.content.UnrelentingCubeEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class UnrelentingCubeEntityRenderer extends EntityRenderer<UnrelentingCubeEntity>
{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Magus.MODID, "textures/entity/magic/unrelenting_cube.png");
	private final CubeModel model = new CubeModel();

	public UnrelentingCubeEntityRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public ResourceLocation getEntityTexture(UnrelentingCubeEntity entity)
	{
		return TEXTURE_LOCATION;
	}

	protected int getBlockLight(WitherSkullEntity entityIn, float partialTicks)
	{
		return 15;
	}

	@Override
	public void render(UnrelentingCubeEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		matrixStackIn.push();
			float size = UnrelentingCubeEntity.SIZE;
			matrixStackIn.scale(size, size, size);
			matrixStackIn.translate(0F, 0.5F, 0F);
			float yaw = MathHelper.rotLerp(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
			float pitch = MathHelper.lerp(partialTicks, entity.prevRotationPitch, entity.rotationPitch);
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(this.getEntityTexture(entity)));
			this.model.rotate(0.0F, yaw, pitch);
			this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		matrixStackIn.pop();
		
		super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}
}
