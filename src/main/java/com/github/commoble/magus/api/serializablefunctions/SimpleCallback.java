package com.github.commoble.magus.api.serializablefunctions;

import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** Convenience class for SerializableCallbacks that only need to remember the function to call and no other data**/
public class SimpleCallback extends SerializableCallback
{
	private final BiConsumer<World, Vec3d> callback;

	public SimpleCallback(ResourceLocation id, BiConsumer<World, Vec3d> callback)
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
	public void accept(World world, Vec3d pos)
	{
		this.callback.accept(world, pos);
	}

}
