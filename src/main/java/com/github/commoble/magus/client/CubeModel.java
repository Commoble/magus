package com.github.commoble.magus.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CubeModel extends Model
{
	private final ModelRenderer renderer;

	public CubeModel()
	{
		super(RenderType::getEntityTranslucent);

		this.textureWidth = 16;
		this.textureHeight = 16;
		this.renderer = new ModelRenderer(this, 0, 0);
		this.renderer.addBox(-8F, -8F, -8F, 16F, 16F, 16F, 0.0F);
		this.renderer.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		this.renderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	public void rotate(float progress, float yaw, float pitch)
	{
		this.renderer.rotateAngleY = yaw * ((float) Math.PI / 180F);
		this.renderer.rotateAngleX = pitch * ((float) Math.PI / 180F);
	}
}
