package com.github.commoble.magus.content;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.github.commoble.magus.BlockTagRegistrar;
import com.github.commoble.magus.api.blocknetworks.BlockNetworkType;
import com.github.commoble.magus.api.blocknetworks.BlockNetworkType.PartialConnection;
import com.github.commoble.magus.api.blocknetworks.BlockNetworks;
import com.github.commoble.magus.api.blocknetworks.ConnectionProvider;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class WizardGritBlock extends Block implements ConnectionProvider
{
	public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.REDSTONE_NORTH;
	public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.REDSTONE_EAST;
	public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.REDSTONE_SOUTH;
	public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.REDSTONE_WEST;
	public static final BooleanProperty BURNT = BooleanProperty.create("burnt");

	public WizardGritBlock(Properties properties)
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, RedstoneSide.NONE).with(EAST, RedstoneSide.NONE).with(SOUTH, RedstoneSide.NONE)
			.with(WEST, RedstoneSide.NONE).with(BURNT, false));
	}

	public BlockNetworkType getNetworkType(BlockState state)
	{
		return isBurnt(state) ? BlockNetworks.BURNT_WIZARD_GRIT : BlockNetworks.WIZARD_GRIT;
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return Blocks.REDSTONE_WIRE.getShape(state, worldIn, pos, context);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		return this.getStateForPlacement(context.getWorld(), context.getPos());
	}

	public BlockState getStateForPlacement(IBlockReader world, BlockPos posPlacedIn)
	{
		return this.getDefaultState().with(WEST, this.getSide(world, posPlacedIn, Direction.WEST)).with(EAST, this.getSide(world, posPlacedIn, Direction.EAST))
			.with(NORTH, this.getSide(world, posPlacedIn, Direction.NORTH)).with(SOUTH, this.getSide(world, posPlacedIn, Direction.SOUTH));
	}

	@Override
	@Deprecated
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onReplaced(state, world, pos, newState, isMoving);
		if (isBurnt(state))
		{
			// list of adjacent burnt wizard grit;
			for (BlockPos adjacentPos : BlockNetworks.BURNT_WIZARD_GRIT.getPartialConnection(world, pos, state).getPotentialConnections())
			{
				world.destroyBlock(adjacentPos, false);
			}
			
		}
	}

	/**
	 * Update the provided state given the provided neighbor facing and neighbor
	 * state, returning a new state. For example, fences make their connections to
	 * the passed in state if possible, and wet concrete powder immediately returns
	 * its solidified counterpart. Note that this method should ideally consider
	 * only the specific face passed in.
	 */
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		if (stateIn.get(BURNT))
		{
			return stateIn;
		}
		if (facing == Direction.DOWN)
		{
			return stateIn;
		}
		else
		{
			return facing == Direction.UP
				? stateIn.with(WEST, this.getSide(worldIn, currentPos, Direction.WEST)).with(EAST, this.getSide(worldIn, currentPos, Direction.EAST))
					.with(NORTH, this.getSide(worldIn, currentPos, Direction.NORTH)).with(SOUTH, this.getSide(worldIn, currentPos, Direction.SOUTH))
				: stateIn.with(RedstoneWireBlock.FACING_PROPERTY_MAP.get(facing), this.getSide(worldIn, currentPos, facing));
		}
	}

	/**
	 * performs updates on diagonal neighbors of the target position and passes in
	 * the flags. The flags can be referenced from the docs for
	 * {@link IWorldWriter#setBlockState(BlockState, BlockPos, int)}.
	 */
	@Override
	@Deprecated
	public void updateDiagonalNeighbors(BlockState state, IWorld world, BlockPos pos, int flags)
	{
		super.updateDiagonalNeighbors(state, world, pos, flags);
		WizardGritBlock.onWizardGritConnectorDiagonalNeighborUpdate(world, pos, flags);
	}

	/**
	 * blocks that wizard grid can connect to up the faces of blocks should call
	 * this in updateDiagonalNeighbors
	 **/
	public static void onWizardGritConnectorDiagonalNeighborUpdate(IWorld world, BlockPos sourcePos, int flags)
	{
		try (BlockPos.PooledMutable mutablePos = BlockPos.PooledMutable.retain())
		{
			for (Direction horizontalDirection : Direction.Plane.HORIZONTAL)
			{
				for (Direction verticalDirection : Direction.Plane.VERTICAL)
				{
					mutablePos.setPos(sourcePos).move(horizontalDirection).move(verticalDirection);
					BlockState oldDiagonalState = world.getBlockState(mutablePos);
					if (BlockTagRegistrar.WIZARD_GRIT_DIAGONAL_CONNECTORS.contains(oldDiagonalState.getBlock()))
					{
						BlockPos targetPos = mutablePos.toImmutable();
						Direction faceDirection = horizontalDirection.getOpposite();
						BlockPos facePos = targetPos.offset(faceDirection);
						// update the wizard grit as if updated by the block just above or below the
						// caller
						BlockState newDiagonalState = oldDiagonalState.updatePostPlacement(faceDirection, world.getBlockState(facePos), world, targetPos, facePos);
						replaceBlock(oldDiagonalState, newDiagonalState, world, targetPos, flags);
					}
				}
			}
		}
	}

	// based on redstone
	private RedstoneSide getSide(IBlockReader world, BlockPos pos, Direction face)
	{
		BlockState thisState = world.getBlockState(pos);
		BlockPos neighborPos = pos.offset(face);
		BlockState neighborState = world.getBlockState(neighborPos);
		BlockPos abovePos = pos.up();
		BlockState aboveState = world.getBlockState(abovePos);
		// this can be called in getStateForPlacement, before the block exists in the
		// world
		// so make sure we're using this specific block instance
		PartialConnection connection = this.getNetworkType(thisState).getPartialConnection(world, pos, this.getDefaultState());

		if (!aboveState.isNormalCube(world, abovePos))
		{
			boolean flag = neighborState.isSolidSide(world, neighborPos, Direction.UP);

			if (flag && connection.isMutuallyConnectedTo(world, neighborPos.up()))
			{
				if (neighborState.isCollisionShapeOpaque(world, neighborPos))
				{
					return RedstoneSide.UP;
				}

				return RedstoneSide.SIDE;
			}
		}

		return !connection.isMutuallyConnectedTo(world, neighborPos)
			&& (neighborState.isNormalCube(world, neighborPos) || !connection.isMutuallyConnectedTo(world, neighborPos.down())) ? RedstoneSide.NONE : RedstoneSide.SIDE;
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
	{
		BlockPos floorPos = pos.down();
		BlockState floorState = worldIn.getBlockState(floorPos);
		return floorState.isSolidSide(worldIn, floorPos, Direction.UP);
	}

	/**
	 * Performs a random tick on a block.
	 */
	@Override
	@Deprecated
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
	{
		super.randomTick(state, worldIn, pos, random);

		// burned wizard grit dissipates naturally after a while
		if (state.has(BURNT) && state.get(BURNT))
		{
			worldIn.removeBlock(pos, false);
		}
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
	{
		if (!worldIn.isRemote)
		{
			if (!state.isValidPosition(worldIn, pos))
			{
				spawnDrops(state, worldIn, pos);
				worldIn.removeBlock(pos, false);
			}

		}
	}

	// protected static boolean canConnectTo(BlockState blockState, IBlockReader
	// world, BlockPos pos, @Nullable Direction side)
	// {
	// return BlockRegistrar.WIZARD_GRIT_CONNECTORS.contains(blockState.getBlock());
	// }

	@Override
	public Set<BlockPos> getPotentialConnections(IBlockReader world, BlockPos thisPos)
	{
		// we are allowed to connect to the following blocks:
		// - the block directly below this one
		// - any NSWE horizontal neighbors, unless they are air blocks
		// - blocks that are directly below a horizontal-neighbor-that-is-an-air-block
		// - blocks that are directly above a horizontal-neighbor-that-is-a-solid-cube,
		// IF the block directly above this is not a solid cube
		Set<BlockPos> connections = new HashSet<>();
		connections.add(thisPos.down());
		BlockPos abovePos = thisPos.up();
		for (Direction horizontalDirection : Direction.Plane.HORIZONTAL)
		{
			BlockPos neighborPos = thisPos.offset(horizontalDirection);
			BlockState neighborState = world.getBlockState(neighborPos);

			if (neighborState.isAir(world, neighborPos))
			{
				connections.add(neighborPos.down());
			}
			else
			{
				connections.add(neighborPos);

				BlockState aboveState = world.getBlockState(abovePos);
				if (!aboveState.isNormalCube(world, abovePos))
				{
					boolean neighborHasSolidTop = neighborState.isSolidSide(world, neighborPos, Direction.UP);
					if (neighborHasSolidTop && neighborState.isCollisionShapeOpaque(world, neighborPos))
					{
						connections.add(neighborPos.up());
					}
				}
			}
		}
		return connections;
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If
	 * inapplicable, returns the passed blockstate.
	 * 
	 * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever
	 *             possible. Implementing/overriding is fine.
	 */
	@Deprecated
	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		switch (rot)
		{
			case CLOCKWISE_180:
				return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
			case CLOCKWISE_90:
				return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
			default:
				return state;
		}
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If
	 * inapplicable, returns the passed blockstate.
	 * 
	 * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever
	 *             possible. Implementing/overriding is fine.
	 */
	@Deprecated
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn)
	{
		switch (mirrorIn)
		{
			case LEFT_RIGHT:
				return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
			case FRONT_BACK:
				return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
			default:
				return super.mirror(state, mirrorIn);
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(NORTH, EAST, SOUTH, WEST, BURNT);
	}

	public static boolean isNotBurnt(BlockState state)
	{
		return state.has(BURNT) && !state.get(BURNT);
	}

	public static boolean isBurnt(BlockState state)
	{
		return state.has(BURNT) && state.get(BURNT);
	}
}
