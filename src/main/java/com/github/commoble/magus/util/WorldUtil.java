package com.github.commoble.magus.util;

import java.util.Map;
import java.util.Random;

import com.github.commoble.magus.BlockRegistrar;
import com.github.commoble.magus.api.blocknetworks.BlockNetworks;
import com.github.commoble.magus.content.MagicCandleBlock;
import com.github.commoble.magus.content.WizardGritBlock;
import com.google.common.collect.Maps;

import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class WorldUtil
{
	public static void snuffWizardGrit(ServerWorld world, BlockPos pos)
	{

		ServerWorld serverWorld = world;
		Random rand = world.getRandom();

		// get all the original states before we set any so we preserve grit shapes
		Map<BlockPos, BlockState> map = Maps.toMap(BlockNetworks.WIZARD_GRIT.getConnectedNetwork(serverWorld, pos), serverWorld::getBlockState);
		map.forEach((gritPos, nextState) -> {
			if (nextState.getBlock() == BlockRegistrar.WIZARD_GRIT.get())
			{
				BlockState burntState = nextState.with(WizardGritBlock.BURNT, true);
				serverWorld.setBlockState(gritPos, burntState);
				serverWorld.spawnParticle(ParticleTypes.SMOKE, gritPos.getX() + 0.5D, gritPos.getY() + 0.5D, gritPos.getZ() + 0.5D, 5, 0.5D, 0.5D, 0.5D, 0.01D);
			}
			else if (nextState.getBlock() == BlockRegistrar.MAGIC_CANDLE.get())
			{
				serverWorld.setBlockState(gritPos, nextState.with(MagicCandleBlock.LIT, false));
				serverWorld.spawnParticle(ParticleTypes.LARGE_SMOKE, gritPos.getX() + 0.5D, gritPos.getY() + 0.5D, gritPos.getZ() + 0.5D, 20, 0.5D, 0.5D, 0.5D, 0.01D);
			}
		});
		serverWorld.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F);

	}
}
