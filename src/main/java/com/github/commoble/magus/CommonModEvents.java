package com.github.commoble.magus;

import com.github.commoble.magus.api.blocknetworks.BlockNetworks;
import com.github.commoble.magus.api.serializablefunctions.CallbackKeys;
import com.github.commoble.magus.api.serializablefunctions.CallbackRegistries;
import com.github.commoble.magus.api.serializablefunctions.SpawnEntityCallback;
import com.github.commoble.magus.content.SimpleCallbacks;
import com.github.commoble.magus.content.WizardGritBlock;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonModEvents
{
	public static void onCommonSetup(FMLCommonSetupEvent event)
	{
		BlockNetworks.WIZARD_GRIT.registerBlockAsItself(BlockRegistrar.WIZARD_GRIT.get(), WizardGritBlock::isNotBurnt);
		BlockNetworks.WIZARD_GRIT.registerBlockAsItself(BlockRegistrar.MAGIC_CANDLE.get());
		
		BlockNetworks.BURNT_WIZARD_GRIT.registerBlockAsItself(BlockRegistrar.WIZARD_GRIT.get(), WizardGritBlock::isBurnt);
		
		
		CallbackRegistries.CALLBACKS.registerSimpleCallback(CallbackKeys.BEE_SWARM, SimpleCallbacks::ejectBeesFromEntity);
		CallbackRegistries.CALLBACKS.registerCallback(CallbackKeys.SPAWN_ENTITY, SpawnEntityCallback::new);
	}
}
