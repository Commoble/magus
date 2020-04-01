package com.github.commoble.magus;

import com.github.commoble.magus.content.CreativeTabs;
import com.github.commoble.magus.content.DebugWandItem;
import com.github.commoble.magus.content.ObjectNames;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistrar
{
	private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Magus.MODID);

	public static void registerRegistry(IEventBus modBus)
	{
		ITEMS.register(modBus);
	}
	
	public static final RegistryObject<BlockItem> WIZARD_GRIT = ITEMS.register(ObjectNames.WIZARD_GRIT, () ->
		new BlockItem(BlockRegistrar.WIZARD_GRIT.get(), new Item.Properties().group(CreativeTabs.MAGUS_ITEMS)));
	public static final RegistryObject<BlockItem> MAGIC_CANDLE = ITEMS.register(ObjectNames.MAGIC_CANDLE, () ->
		new BlockItem(BlockRegistrar.MAGIC_CANDLE.get(), new Item.Properties().group(CreativeTabs.MAGUS_ITEMS)));
	
	public static final RegistryObject<DebugWandItem> DEBUG_WAND = ITEMS.register(ObjectNames.DEBUG_WAND, () -> 
		new DebugWandItem(new Item.Properties().group(CreativeTabs.MAGUS_ITEMS)));
}
