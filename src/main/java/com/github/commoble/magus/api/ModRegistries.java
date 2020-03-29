package com.github.commoble.magus.api;

import com.github.commoble.magus.Magus;
import com.github.commoble.magus.api.serializablefunctions.CallbackFactory;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

/** class for holding custom IForgeRegistries **/
public class ModRegistries
{
	public static IForgeRegistry<CallbackFactory> CALLBACKS;
	
	public static void onRegistryInit()
	{
		CALLBACKS = new RegistryBuilder<CallbackFactory>()
			.setName(new ResourceLocation(Magus.MODID, "callback_factories"))
			.setType(CallbackFactory.class)
			.create();
	}
}
