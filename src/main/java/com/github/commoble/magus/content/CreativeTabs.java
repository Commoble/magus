package com.github.commoble.magus.content;

import com.github.commoble.magus.Magus;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CreativeTabs
{
	public static final ItemGroup TAB = new ItemGroup(Magus.MODID) {
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(Items.DIAMOND);
		}
	};
}
