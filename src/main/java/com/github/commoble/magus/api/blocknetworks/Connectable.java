package com.github.commoble.magus.api.blocknetworks;

import java.util.function.Predicate;

import net.minecraft.block.BlockState;

/**
 * Data type registered to block instance through a BlockNetworkType, conjoining the blockstate predicate and the set provider.
 * Determines whether a block's blockstate permits it to be part of a block network, as well as
 * the block positions a block is allowed to connect to in that network
 */
public class Connectable implements Predicate<BlockState>
{
	private final ConnectionProvider provider;
	private final Predicate<BlockState> statePredicate;
	
	public Connectable(ConnectionProvider provider, Predicate<BlockState> statePredicate)
	{
		this.provider = provider;
		this.statePredicate = statePredicate;
	}
	
	public ConnectionProvider getConnectionProvider()
	{
		return this.provider;
	}

	@Override
	public boolean test(BlockState state)
	{
		return this.statePredicate.test(state);
	}
}
