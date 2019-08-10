package com.github.commoble.magus;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockRegistrar
{
	public static void registerBlocks(IForgeRegistry<Block> registry)
	{
		RegistryEventHandler.register(registry, new PlaceholderBlock(Block.Properties.create(Material.CLAY)), BlockNames.PLACEHOLDER);
	}
}
