package com.github.commoble.magus.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

/**
 * Used for determining whether blocks connect to form a wizard grit network
 */
public class WizardGritConnectors
{
	private static final Map<Block, WizardGritConnectionProvider> REGISTRY = new HashMap<>();

	public static final Set<BlockPos> EMPTY_SET = ImmutableSet.of();
	public static final WizardGritConnectionProvider NULL_SET_PROVIDER = (world, pos) -> EMPTY_SET;

	/**
	 * Add blocks to the registry *after* all blocks have been registered!
	 * FMLCommonSetupEvent is a good time to do it If a provider was previously
	 * registered, returns it, otherwise returns null
	 **/
	@Nullable
	public static WizardGritConnectionProvider registerConnectionProvider(@Nonnull Block block, @Nonnull WizardGritConnectionProvider provider)
	{
		return REGISTRY.put(block, provider);
	}

	/**
	 * Convenience method to register a block that is its own connection provider
	 * (not required) If a provider was previously registered, returns it, otherwise
	 * returns null
	 **/
	@Nullable
	public static <T extends Block & WizardGritConnectionProvider> WizardGritConnectionProvider registerBlockAsItself(@Nonnull T block)
	{
		return registerConnectionProvider(block, block);
	}

	/**
	 * Gets a provider from the registry, returning (()->empty set) if no provider
	 * is registered to the given block
	 **/
	@Nonnull
	public static WizardGritConnectionProvider getConnectionProvider(@Nonnull Block block)
	{
		return REGISTRY.getOrDefault(block, NULL_SET_PROVIDER);
	}

	@Nonnull
	/**
	 * Gets a provider from the registry for the block at the given position,
	 * returning (()->empty set) if no provider is registered to the given block
	 **/
	public static WizardGritConnectionProvider getConnectionProvider(@Nonnull IBlockReader world, @Nonnull BlockPos pos)
	{
		return getConnectionProvider(world.getBlockState(pos).getBlock());
	}

	@Nonnull
	public static Set<BlockPos> getPotentialConnections(@Nonnull IBlockReader world, @Nonnull BlockPos pos)
	{
		return getConnectionProvider(world, pos).getPotentialConnections(world, pos);
	}

	public static boolean doesPotentialConnectionExist(@Nonnull IBlockReader world, @Nonnull BlockPos posA, @Nonnull BlockPos posB)
	{
		return getPotentialConnections(world, posA).contains(posB);
	}

	/**
	 * Returns a conjoined blockpos and the positions it potentially connects to
	 **/
	@Nonnull
	public static PartialConnection getPartialConnection(@Nonnull IBlockReader world, @Nonnull BlockPos pos)
	{
		return new PartialConnection(world, pos);
	}

	/**
	 * Use a specific block for the connection provider instead of the one at the
	 * given position -- useful for when a block is *about* to be placed
	 **/
	@Nonnull
	public static PartialConnection getPartialConnection(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull Block block)
	{
		return new PartialConnection(world, pos, block);
	}

	/**
	 * Returns the set of all positions that A) the given position connects to, and
	 * B) connect to the given position
	 **/
	@Nonnull
	public static Set<BlockPos> getMutualConnections(@Nonnull IBlockReader world, @Nonnull BlockPos pos)
	{
		return getPotentialConnections(world, pos).stream()
			.filter(queryPos -> doesPotentialConnectionExist(world, queryPos, pos))
			.collect(Collectors.toSet());
	}

	/**
	 * Returns true if the blocks at the given positions are registered connectors
	 * and can connect to each other, false otherwise
	 **/
	public static boolean areTwoBlocksConnected(@Nonnull IBlockReader world, @Nonnull BlockPos posA, @Nonnull BlockPos posB)
	{
		return doesPotentialConnectionExist(world, posA, posB) && doesPotentialConnectionExist(world, posB, posA);
	}

	/**
	 * Returns the set of all block positions networked to the original position
	 **/
	@Nonnull
	public static Set<BlockPos> getConnectedNetwork(@Nonnull IBlockReader world, @Nonnull BlockPos startPos)
	{
		return buildConnectedNetwork(world, startPos, new HashSet<>());
	}

	@Nonnull
	private static Set<BlockPos> buildConnectedNetwork(@Nonnull IBlockReader world, @Nonnull BlockPos startPos, @Nonnull Set<BlockPos> mutableSet)
	{
		mutableSet.add(startPos);
		for (BlockPos nextPos : getMutualConnections(world, startPos))
		{
			if (!mutableSet.contains(nextPos))
			{
				buildConnectedNetwork(world, nextPos, mutableSet);
			}
		}
		return mutableSet;
	}

	/**
	 * data class for storing a source block and its known potential connections,
	 * whose purposes are A) prevent recalculation more often than necessary, and B)
	 * to ensure that a connection set has been verified to be a given blockpos's
	 * potential connections for best results, do not store instances of this in
	 * fields; only use as method arguments
	 *
	 */
	public static class PartialConnection
	{
		private final BlockPos sourcePos;
		private final Set<BlockPos> potentialConnections;

		private PartialConnection(@Nonnull IBlockReader world, @Nonnull BlockPos pos)
		{
			this.sourcePos = pos;
			this.potentialConnections = WizardGritConnectors.getPotentialConnections(world, pos);
		}

		/**
		 * Use a specific block for the connection provider instead of the one at the
		 * given position -- useful for when a block is *about* to be placed
		 **/
		private PartialConnection(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull Block block)
		{
			this.sourcePos = pos;
			this.potentialConnections = WizardGritConnectors.getConnectionProvider(block).getPotentialConnections(world, pos);
		}

		@Nonnull
		public BlockPos getSourcePos()
		{
			return this.sourcePos;
		}

		@Nonnull
		public Set<BlockPos> getPotentialConnections()
		{
			return this.potentialConnections;
		}

		public boolean isMutuallyConnectedTo(@Nonnull IBlockReader world, @Nonnull BlockPos targetPos)
		{
			return this.getPotentialConnections().contains(targetPos) && WizardGritConnectors.doesPotentialConnectionExist(world, targetPos, this.getSourcePos());
		}
	}
}
