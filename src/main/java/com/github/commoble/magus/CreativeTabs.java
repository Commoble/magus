package com.github.commoble.magus;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ObjectHolder;

public class CreativeTabs
{
	@ObjectHolder(Magus.MODID + ":" + BlockNames.PLACEHOLDER)
	public static final Item PLACEHOLDER = null;

	// creative tab for the stuff
	public static final ItemGroup tab = new ItemGroup(Magus.MODID) {
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(PLACEHOLDER);
		}
	};

}