package com.github.commoble.magus.api;

import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * RegistryObject-like entry for use with custom forge registries that don't work with DeferredRegisters
 * Registry-replacement should be safe, but accessing this before registry concludes is not!
 */
public class CachedEntry<T extends IForgeRegistryEntry<T>> implements Supplier<T>
{
	private final ResourceLocation id;
	private final IForgeRegistry<T> registry;
	
	private T cachedObject = null;
	
	public CachedEntry(ResourceLocation id, IForgeRegistry<T> registry)
	{
		this.id = id;
		this.registry = registry;
	}

	@Override
	public T get()
	{
		if (this.cachedObject == null)
		{
			this.cachedObject = this.registry.getValue(this.id);
		}
		return this.cachedObject;
	}
}
