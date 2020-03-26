package com.github.commoble.magus;

import com.github.commoble.magus.api.WizardGritConnectors;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonModEvents
{
	public static void onCommonSetup(FMLCommonSetupEvent event)
	{
		WizardGritConnectors.registerBlockAsItself(BlockRegistrar.WIZARD_GRIT.get());
		WizardGritConnectors.registerBlockAsItself(BlockRegistrar.MAGIC_CANDLE.get());
	}
}
