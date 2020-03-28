package com.github.commoble.magus.api.serializablefunctions;

import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Convenience class for SerializableCallbacks that only need to remember the function to call and no other data**/
public class SimpleCallback extends SerializableCallback
{
	private final BiConsumer<World, BlockPos> callback;

	public SimpleCallback(ResourceLocation id, BiConsumer<World, BlockPos> callback)
	{
		super(id, new CompoundNBT());
		this.callback = callback;
	}

	@Override
	public void deserializeExtraData(CompoundNBT extraData)
	{
	}

	@Override
	@Nonnull
	public CompoundNBT serializeExtraData()
	{
		return new CompoundNBT();
	}

	@Override
	public void accept(World world, BlockPos pos)
	{
		this.callback.accept(world, pos);
	}

}
