package com.github.commoble.magus.api.serializablefunctions;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class NoCallback extends SerializableCallback
{
	private static final ResourceLocation ID = new ResourceLocation("magus:nocallback");
	
	private static final NoCallback NOPE = new NoCallback();
	
	public static SerializableCallback getEmptyCallback()
	{
		return NOPE;
	}
	
	private NoCallback()
	{
		super(ID, new CompoundNBT());
	}

	@Override
	public CompoundNBT serializeExtraData()
	{
		return new CompoundNBT();
	}

	@Override
	public void deserializeExtraData(CompoundNBT extraData)
	{		
	}

	@Override
	public void accept(World world, BlockPos pos)
	{
		// NOPE
	}
	
}