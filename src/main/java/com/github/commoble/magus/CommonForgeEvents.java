package com.github.commoble.magus;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class CommonForgeEvents
{
	// event hooks
	
	public static void onBlockActivated(PlayerInteractEvent.RightClickBlock event)
	{
		World world = event.getWorld();
		if (!world.isRemote())
		{
			BlockPos pos = event.getPos();
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() == Blocks.BELL)
			{
				onRingBell(world, state, pos, event.getHand(), event.getFace());
			}
		}
	}
	
	// subroutines
	
	public static void onRingBell(World world, BlockState state, BlockPos pos, Hand hand, Direction face)
	{
//	      boolean flag = Blocks.BELL.canRingFrom(state, face, p_226884_3_.getHitVec().y - (double)pos.getY());
//	      if (flag) {
//	         boolean flag1 = this.func_226885_a_(p_226884_1_, pos, face);
//	         if (flag1 && p_226884_4_ != null) {
//	            p_226884_4_.addStat(Stats.BELL_RING);
//	         }
//
//	         return true;
//	      } else {
//	         return false;
//	      }
	}
}
