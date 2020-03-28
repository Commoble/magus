package com.github.commoble.magus.api.serializablefunctions;

import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Interface for storing callbacks in entities n' things that can be performed later **/
public abstract class SerializableCallback implements BiConsumer<World, BlockPos>
{
	private final ResourceLocation id;
	
	/** use SimpleCallback<T> if extraData is not needed **/
	public SerializableCallback(@Nonnull ResourceLocation id, @Nonnull CompoundNBT extraData)
	{
		this.id = id;
		this.deserializeExtraData(extraData);
	}
	
	public ResourceLocation getRegistryName()
	{
		return this.id;
	}
	
	/** returns an NBT that can be fed into CallbackRegistry::deserialize method to get the same callback **/
	@Nonnull
	public CompoundNBT serialize()
	{
		CompoundNBT out = new CompoundNBT();
		out.putString(CallbackRegistry.TYPE, this.getRegistryName().toString());
		out.put(CallbackRegistry.DATA, this.serializeExtraData());
		return out;
	}

	@Nonnull
	public abstract CompoundNBT serializeExtraData();

	public abstract void deserializeExtraData(CompoundNBT extraData);

	@Override
	public abstract void accept(World world, BlockPos pos);


}
