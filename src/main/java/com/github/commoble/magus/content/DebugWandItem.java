package com.github.commoble.magus.content;

import com.github.commoble.magus.content.entities.UnrelentingCubeEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DebugWandItem extends Item
{

	public DebugWandItem(Properties properties)
	{
		super(properties);
	}

	/**
	 * Called to trigger the item's "innate" right click behavior. To handle when
	 * this item is used on a Block, see {@link #onItemUse}.
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
	{
		UnrelentingCubeEntity.launchAsProjectile(playerIn, worldIn);

		return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
	}
}
