package com.github.commoble.magus.api;

import java.util.function.Supplier;

/**
 * RegistryObject-like delegate for non-forge registries. Gets the object from the given map the first time it's called.
 * 
 * TODO write things to update-cache-on-registry-replacement.
 * This shouldn't be a problem before then as long as nobody calls get() until after all things are registered.
 */
public abstract class CachedEntry<V> implements Supplier<V>
{
	protected V value = null;
	
	protected abstract V getNewValue();

	@Override
	public V get()
	{
		if (this.value == null)
		{
			this.value = this.getNewValue();
		}
		return this.value;
	}
	
	
}
