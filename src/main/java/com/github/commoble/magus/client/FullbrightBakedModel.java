// this class is heavily based on FullbrightBakedModel from Refined Storage
// https://github.com/raoulvdberge/refinedstorage/blob/mc1.15/src/main/java/com/raoulvdberge/refinedstorage/render/model/FullbrightBakedModel.java

/**
Copyright © 2015 - 2020 Refined Storage contributors

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.commoble.magus.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;


/**
 *	this class works by wrapping around a base model (e.g. from a block model json) and a list of textures to fullbrighten
 *	when models are baked, the original model should be replaced with this model and the texture list
 *	When this model is queried for its quads, it gets the quads from the base model and lightens the vertices of the relevant sides
 *	(quads are cached appropriately)
 *
 *	To declare fullbright textures, replace an existing model with this one in ModelBakeEvent
 */
public class FullbrightBakedModel implements IBakedModel
{
	private static final LoadingCache<FaceKey, List<BakedQuad>> QUAD_CACHE = CacheBuilder.newBuilder().build(new CacheLoader<FaceKey, List<BakedQuad>>()
	{
		@Override
		public List<BakedQuad> load(FaceKey key)
		{
			return lightQuads(key.model.getQuads(key.state, key.side, key.rand, EmptyModelData.INSTANCE), key.textures);
		}
	});

	public static final int FULLBRIGHT = LightTexture.packLight(15, 15);

	protected final IBakedModel base;
	private final Set<ResourceLocation> fullbrightTextures;

	public FullbrightBakedModel(IBakedModel base, ResourceLocation... fullbrightTextures)
	{
		this.base = base;
		this.fullbrightTextures = new HashSet<>(Arrays.asList(fullbrightTextures));
	}

	@Override
	@Deprecated
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
	{
		if (state == null)
		{
			return this.base.getQuads(state, side, rand);
		}

		return QUAD_CACHE.getUnchecked(new FaceKey(state, side, this.base, this.fullbrightTextures, rand));
	}

	private static List<BakedQuad> lightQuads(List<BakedQuad> oldQuads, Set<ResourceLocation> textures)
	{
		List<BakedQuad> newQuads = new ArrayList<>(oldQuads);

		int quadCount = newQuads.size();
		for (int i = 0; i < quadCount; i++)
		{
			BakedQuad quad = newQuads.get(i);

			if (textures.contains(quad.func_187508_a().getName()))
			{
				newQuads.set(i, lightQuad(quad));
			}
		}

		return newQuads;
	}

	private static BakedQuad lightQuad(BakedQuad quad)
	{
		int[] vertexData = quad.getVertexData().clone();

		// set lighting to fullbright on all vertices
		vertexData[6] = FULLBRIGHT;
		vertexData[6 + 8] = FULLBRIGHT;
		vertexData[6 + 8 + 8] = FULLBRIGHT;
		vertexData[6 + 8 + 8 + 8] = FULLBRIGHT;

		return new BakedQuad(vertexData, quad.getTintIndex(), quad.getFace(), quad.func_187508_a(), quad.shouldApplyDiffuseLighting());
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return this.base.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d()
	{
		return this.base.isGui3d();
	}

	@Override
	public boolean func_230044_c_()
	{
		return this.base.func_230044_c_();
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return this.base.isBuiltInRenderer();
	}

	@Override
	@Deprecated
	public TextureAtlasSprite getParticleTexture()
	{
		return this.base.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return this.base.getOverrides();
	}

	// used to get cached quads from quad cache
	private static class FaceKey
	{
		// used for key
		private final BlockState state;
		private final Direction side;

		// not used for key
		private final IBakedModel model;
		private final Set<ResourceLocation> textures;
		private final Random rand;

		public FaceKey(@Nonnull BlockState state, @Nullable Direction side, IBakedModel model, Set<ResourceLocation> textures, Random rand)
		{
			this.state = state;
			this.side = side;
			this.model = model;
			this.textures = textures;
			this.rand = rand;
		}

		@Override
		public boolean equals(Object other)
		{
			if (this == other)
			{
				return true;
			}
			else if (!(other instanceof FaceKey))
			{
				return false;
			}
			else
			{
				FaceKey otherKey = (FaceKey) other;
				return this.side == otherKey.side && this.state.equals(otherKey.state);
			}
		}

		@Override
		public int hashCode()
		{
			return this.state.hashCode() + 113 * (this.side == null ? 0 : this.side.hashCode());
		}
	}
}
