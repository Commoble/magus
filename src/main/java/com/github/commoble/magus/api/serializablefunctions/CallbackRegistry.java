package com.github.commoble.magus.api.serializablefunctions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CallbackRegistry
{
	public static final String TYPE = "type";
	public static final String DATA = "data";
	
	private final Map<ResourceLocation, BiFunction<ResourceLocation, CompoundNBT, SerializableCallback>> factories = new HashMap<>();
	
	/**
	 * The "factory" specified here can generally be YourSerializableCallback::new.
	 * Call this in FMLCommonSetupEvent
	 */
	public void registerCallback(@Nonnull ResourceLocation registryName, @Nonnull BiFunction<ResourceLocation, CompoundNBT, SerializableCallback> factory)
	{
		this.factories.put(registryName, factory);
	}
	
	public void registerSimpleCallback(@Nonnull ResourceLocation registryName, BiConsumer<World, BlockPos> callback)
	{
		this.registerCallback(registryName, (id, nbt) -> new SimpleCallback(id, callback));
	}
	
	@Nonnull
	public SerializableCallback createCallback(ResourceLocation id, CompoundNBT extraData)
	{
		BiFunction<ResourceLocation, CompoundNBT, SerializableCallback> factory = this.factories.get(id);
		if (factory == null)
		{
			return NoCallback.getEmptyCallback();
		}
		else
		{
			return factory.apply(id, extraData);
		}
	}
	
	/**
	 * nbt should be an nbt compound that contains the following object:
	 * 
	 * 	{
	 * 		"type": "resource:location",
	 *		"data": {EXTRADATA_OBJECT}
	 * 	}
	 * 
	 * calling SerializableCallback::serialize() will produce such an NBT
	 */
	@Nonnull
	public SerializableCallback deserialize(@Nonnull CompoundNBT nbt)
	{
		return this.createCallback(new ResourceLocation(nbt.getString(TYPE)), nbt.getCompound(DATA));
	}
}
