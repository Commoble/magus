package com.github.commoble.magus.content;

import java.util.function.Supplier;

import com.github.commoble.magus.ItemRegistrar;
import com.github.commoble.magus.Magus;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CreativeTabs
{
	public static final ItemGroup MAGUS_ITEMS = new ModItemGroup(Magus.MODID, ItemRegistrar.WIZARD_GRIT);
	public static final ItemGroup MAGUS_BOOKS = new ModItemGroup(Magus.MODID + ".books", () -> Items.BOOK);
	
	public static class ModItemGroup extends ItemGroup
	{
		private final Supplier<? extends Item> itemGetter;
		
		public ModItemGroup(String name, Supplier<? extends Item> itemGetter)
		{
			super(name);
			this.itemGetter = itemGetter;
		}
		
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(this.itemGetter.get());
		}
	}
}
