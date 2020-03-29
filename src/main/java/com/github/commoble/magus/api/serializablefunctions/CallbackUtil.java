package com.github.commoble.magus.api.serializablefunctions;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import com.github.commoble.magus.api.CachedEntry;
import com.github.commoble.magus.api.ModRegistries;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CallbackUtil
{
	public static final String TYPE = "type";
	public static final String DATA = "data";
	
	/**
	 * The "factory" specified here can generally be YourSerializableCallback::new.
	 * Call this in RegistryEvent.Register<CallbackFactory>
	 */
	@Nonnull
	public static CachedEntry<CallbackFactory> registerCallback(@Nonnull ResourceLocation registryName, @Nonnull BiFunction<ResourceLocation, CompoundNBT, SerializableCallback> factory)
	{
		ModRegistries.CALLBACKS.register(new CallbackFactory(registryName, factory));
		return new CachedEntry<>(registryName, ModRegistries.CALLBACKS);
	}
	
	@Nonnull
	public static CachedEntry<CallbackFactory> registerSimpleCallback(@Nonnull ResourceLocation registryName, BiConsumer<World, Vec3d> callback)
	{
		return registerCallback(registryName, (id, nbt) -> new SimpleCallback(id, callback));
	}

	
	@Nonnull
	public static SerializableCallback createCallback(ResourceLocation id, CompoundNBT extraData)
	{
		return ModRegistries.CALLBACKS.getValue(id).apply(extraData);
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
	public static SerializableCallback deserialize(@Nonnull CompoundNBT nbt)
	{
		return createCallback(new ResourceLocation(nbt.getString(TYPE)), nbt.getCompound(DATA));
	}
}
