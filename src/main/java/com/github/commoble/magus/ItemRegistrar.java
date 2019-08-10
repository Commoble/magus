package com.github.commoble.magus;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Magus.MODID)
public class ItemRegistrar
{
	@ObjectHolder(BlockNames.PLACEHOLDER)
	public static final Block PLACEHOLDER = null;
	
	public static void registerItems(IForgeRegistry<Item> registry)
	{
		RegistryEventHandler.register(registry, new BlockItem(PLACEHOLDER, new Item.Properties().group(CreativeTabs.tab)), BlockNames.PLACEHOLDER);
	}
}
