package com.github.commoble.magus.content;

import com.github.commoble.magus.network.OpenLoreBookS2CPacket;
import com.github.commoble.magus.network.PacketHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class LoreBookItem extends Item
{
	public LoreBookItem(Properties properties)
	{
		super(properties);
	}

	/**
	 * Called to trigger the item's "innate" right click behavior. To handle when
	 * this item is used on a Block, see {@link #onItemUse}.
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		if (player instanceof ServerPlayerEntity)
		{
			PacketHandler.INSTANCE.send(
				PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player),
				new OpenLoreBookS2CPacket(new ResourceLocation("magus:test_book")));
		}	
		return ActionResult.resultConsume(player.getHeldItem(hand));
	}

}
