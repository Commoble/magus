package com.github.commoble.magus;

import com.github.commoble.magus.api.ModRegistries;
import com.github.commoble.magus.api.blocknetworks.BlockNetworks;
import com.github.commoble.magus.api.serializablefunctions.CallbackFactory;
import com.github.commoble.magus.content.WizardGritBlock;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonModEvents
{
	public static void subscribeEvents(IEventBus modBus)
	{
		modBus.addListener(CommonModEvents::onRegisterRegistries);
		modBus.addGenericListener(CallbackFactory.class, CommonModEvents::onRegisterCallbacks);
		modBus.addListener(CommonModEvents::onCommonSetup);
	}
	
	public static void onRegisterRegistries(RegistryEvent.NewRegistry event)
	{
		ModRegistries.onRegistryInit();
	}
	
	// deferred registries don't work with custom forge registries, have to do it the old-fashioned way
	public static void onRegisterCallbacks(RegistryEvent.Register<CallbackFactory> event)
	{
		CallbackRegistrar.registerCallbacks();
	}
	
	public static void onCommonSetup(FMLCommonSetupEvent event)
	{
		BlockNetworks.WIZARD_GRIT.registerBlockAsItself(BlockRegistrar.WIZARD_GRIT.get(), WizardGritBlock::isNotBurnt);
		BlockNetworks.WIZARD_GRIT.registerBlockAsItself(BlockRegistrar.MAGIC_CANDLE.get());
		
		BlockNetworks.BURNT_WIZARD_GRIT.registerBlockAsItself(BlockRegistrar.WIZARD_GRIT.get(), WizardGritBlock::isBurnt);
	}
}
