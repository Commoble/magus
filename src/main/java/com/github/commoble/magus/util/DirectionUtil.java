package com.github.commoble.magus.util;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.util.Direction;

public class DirectionUtil
{
	public static final Set<Direction> HORIZONTALS = EnumSet.of(
		Direction.NORTH,
		Direction.SOUTH,
		Direction.WEST,
		Direction.EAST);
	
	public static final Set<Direction> HORIZONTALS_AND_DOWN = EnumSet.of(
		Direction.DOWN,
		Direction.NORTH,
		Direction.SOUTH,
		Direction.WEST,
		Direction.EAST);
}
