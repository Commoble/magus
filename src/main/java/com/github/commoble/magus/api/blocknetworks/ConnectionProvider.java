package com.github.commoble.magus.api.blocknetworks;

import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

@FunctionalInterface
public interface ConnectionProvider
{
	/**
	 * Returns the set of BlockPos that a given block can connect to as part of a network.
	 * Two blockpos are considered to be connected if the blockpos sets given by the blocks
	 * at those positions each contain the other position.
	 * The returned set cannot be null; return an empty set instead.
	 * 
	 * Ideally this should not be called directly; get the set via BlockNetworkType,
	 * as it will filter out additional positions
	 */
	@Nonnull
	public Set<BlockPos> getPotentialConnections(IBlockReader world, BlockPos thisPos);
}