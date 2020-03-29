package com.github.commoble.magus.api.serializablefunctions;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class CallbackFactory extends ForgeRegistryEntry<CallbackFactory> implements Function<CompoundNBT, SerializableCallback>
{
	private BiFunction<ResourceLocation, CompoundNBT, SerializableCallback> factory;
	
	public CallbackFactory(ResourceLocation id, BiFunction<ResourceLocation, CompoundNBT, SerializableCallback> factory)
	{
		this.setRegistryName(id);
	}

	@Override
	public SerializableCallback apply(CompoundNBT extraData)
	{
		return this.factory.apply(this.getRegistryName(), extraData);
	}
}
