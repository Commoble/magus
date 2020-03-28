package com.github.commoble.magus.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class InvisibleEntityRenderer<T extends Entity> extends EntityRenderer<T>
{
	protected InvisibleEntityRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public ResourceLocation getEntityTexture(T entity)
	{
		return MissingTextureSprite.getLocation();
	}

	@Override
	public boolean shouldRender(T livingEntityIn, ClippingHelperImpl camera, double camX, double camY, double camZ)
	{
		return false;
	}

	@Override
	public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		
	}

	@Override
	protected boolean canRenderName(T entity)
	{
		return false;
	}
}
