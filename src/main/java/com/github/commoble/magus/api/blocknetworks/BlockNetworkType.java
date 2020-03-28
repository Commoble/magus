package com.github.commoble.magus.api.blocknetworks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

/**
 * Used for determining whether blocks connect to form a contiguous network
 */
public class BlockNetworkType
{
	public static final Set<BlockPos> EMPTY_SET = ImmutableSet.of();
	public static final ConnectionProvider NULL_SET_PROVIDER = (world, pos) -> EMPTY_SET;
	public static final Connectable NULL_CONNECTABLE = new Connectable(NULL_SET_PROVIDER, Predicates.alwaysFalse());
	
	private final Map<Block, Connectable> REGISTRY = new HashMap<>();

	/**
	 * Add blocks to the registry *after* all blocks have been registered!
	 * FMLCommonSetupEvent is a good time to do it
	 * If a Connectable was previously registered, returns it, otherwise returns null
	 **/
	@Nullable
	public Connectable registerConnectable(@Nonnull Block block, @Nonnull ConnectionProvider provider, @Nonnull Predicate<BlockState> statePredicate)
	{
		return this.REGISTRY.put(block, new Connectable(provider, statePredicate));
	}

	/**
	 * Convenience method to register a block that is its own connection provider
	 * (not required). If a Connectable was previously registered, returns it, otherwise
	 * returns null
	 **/
	@Nullable
	public <T extends Block & ConnectionProvider> Connectable registerBlockAsItself(@Nonnull T block, @Nonnull Predicate<BlockState> statePredicate)
	{
		return this.registerConnectable(block, block, statePredicate);
	}
	
	/**
	 * Convenience method to register a block that is its own connection provider
	 * (not required) and whose ability to be part of the network doesn't depend on its blockstate.
	 * If a Connectable was previously registered, returns it, otherwise returns null
	 **/
	@Nullable
	public <T extends Block & ConnectionProvider> Connectable registerBlockAsItself(@Nonnull T block)
	{
		return this.registerConnectable(block, block, Predicates.alwaysTrue());
	}
	
	/**
	 * Gets a connectable object from the registry, returning (()->empty set) if no connectable
	 * is registered to the given block
	 */
	@Nonnull
	public Connectable getConnectable(@Nonnull Block block)
	{
		return this.REGISTRY.getOrDefault(block, NULL_CONNECTABLE);
	}
	
	/** Returns true if a blockstate at a given position may connect to this network **/
	public boolean isBlockStateValid(@Nonnull IBlockReader world, BlockPos pos)
	{
		return this.isBlockStateValid(world.getBlockState(pos));
	}
	
	/** Returns true if a given blockstate may connect to this network **/
	public boolean isBlockStateValid(@Nonnull BlockState state)
	{
		return this.getConnectable(state.getBlock()).test(state);
	}

	/**
	 * Gets a provider from the registry, returning (()->empty set) if no provider
	 * is registered to the given block
	 **/
	@Nonnull
	public ConnectionProvider getConnectionProvider(@Nonnull Block block)
	{
		return this.REGISTRY.getOrDefault(block, NULL_CONNECTABLE).getConnectionProvider();
	}

	/**
	 * Gets a provider from the registry for the block at the given position,
	 * returning (()->empty set) if no provider is registered to the given block
	 **/
	@Nonnull
	public ConnectionProvider getConnectionProvider(@Nonnull IBlockReader world, @Nonnull BlockPos pos)
	{
		return this.getConnectionProvider(world.getBlockState(pos).getBlock());
	}

	/**
	 * Gets the set of potential connections that a block at a given position can connect to.
	 * If the target block is not part of this network, an empty set is returned.
	 * Only positions that contain blockstates that are part of this network are returned.
	 */
	@Nonnull
	public Set<BlockPos> getPotentialConnections(@Nonnull IBlockReader world, @Nonnull BlockPos pos)
	{
		return this.getConnectionProvider(world, pos).getPotentialConnections(world, pos).stream()
			.filter(queryPos -> this.isBlockStateValid(world, queryPos))
			.collect(Collectors.toSet());
	}
	
	/**
	 * Gets the set of potential connectors that a block would connect to if it were at a given position.
	 * If the specified block is not part of this network, an empty set is returned.
	 * Only positions that contain blockstates that are part of this network are returned.
	 */
	@Nonnull
	public Set<BlockPos> getPotentialConnections(@Nonnull IBlockReader world, @Nonnull BlockPos pos, BlockState state)
	{
		return this.getConnectionProvider(state.getBlock()).getPotentialConnections(world, pos).stream()
			.filter(queryPos -> this.isBlockStateValid(world, queryPos))
			.collect(Collectors.toSet());
	}

	/** Returns true if both blocks are part of this network and the block at posA is allowed to connect to posB **/
	public boolean doesPotentialConnectionExist(@Nonnull IBlockReader world, @Nonnull BlockPos posA, @Nonnull BlockPos posB)
	{ 
		return this.getPotentialConnections(world, posA).contains(posB);
	}

	/**
	 * Returns a blockpos and the positions it potentially connects to
	 **/
	@Nonnull
	public PartialConnection getPartialConnection(@Nonnull IBlockReader world, @Nonnull BlockPos pos)
	{
		return new PartialConnection(this, world, pos);
	}

	/**
	 * Use a specific blockstate for the connection provider instead of the one at the
	 * given position -- useful for when a block is *about* to be placed
	 **/
	@Nonnull
	public PartialConnection getPartialConnection(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state)
	{
		return new PartialConnection(this, world, pos, state);
	}

	/**
	 * Returns the set of all positions that A) the given position connects to, and
	 * B) connect to the given position
	 **/
	@Nonnull
	public Set<BlockPos> getMutualConnections(@Nonnull IBlockReader world, @Nonnull BlockPos pos)
	{
		return this.getPotentialConnections(world, pos).stream()
			.filter(queryPos -> this.doesPotentialConnectionExist(world, queryPos, pos))
			.collect(Collectors.toSet());
	}

	/**
	 * Returns true if the blocks at the given positions are registered connectors
	 * and can connect to each other, false otherwise
	 **/
	public boolean areTwoBlocksConnected(@Nonnull IBlockReader world, @Nonnull BlockPos posA, @Nonnull BlockPos posB)
	{
		return this.doesPotentialConnectionExist(world, posA, posB) && this.doesPotentialConnectionExist(world, posB, posA);
	}

	/**
	 * Returns the set of all block positions networked to the original position
	 **/
	@Nonnull
	public Set<BlockPos> getConnectedNetwork(@Nonnull IBlockReader world, @Nonnull BlockPos startPos)
	{
		return this.buildConnectedNetwork(world, startPos, new HashSet<>());
	}

	@Nonnull
	private Set<BlockPos> buildConnectedNetwork(@Nonnull IBlockReader world, @Nonnull BlockPos startPos, @Nonnull Set<BlockPos> mutableSet)
	{
		mutableSet.add(startPos);
		for (BlockPos nextPos : this.getMutualConnections(world, startPos))
		{
			if (!mutableSet.contains(nextPos))
			{
				this.buildConnectedNetwork(world, nextPos, mutableSet);
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
		private final BlockNetworkType network;
		private final BlockPos sourcePos;
		private final Set<BlockPos> potentialConnections;

		private PartialConnection(BlockNetworkType network, @Nonnull IBlockReader world, @Nonnull BlockPos pos)
		{
			this.network = network;
			this.sourcePos = pos;
			this.potentialConnections = this.network.getPotentialConnections(world, pos);
		}

		/**
		 * Use a specific blockstate for the connection provider instead of the one at the
		 * given position -- useful for when a block is *about* to be placed, or after it has been destroyed
		 **/
		private PartialConnection(BlockNetworkType network, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state)
		{
			this.network = network;
			this.sourcePos = pos;
			this.potentialConnections = this.network.getPotentialConnections(world, pos, state);
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
			BlockState targetState = world.getBlockState(targetPos);
			return this.getPotentialConnections().contains(targetPos)
				&& this.network.doesPotentialConnectionExist(world, targetPos, this.getSourcePos());
		}
	}
}
