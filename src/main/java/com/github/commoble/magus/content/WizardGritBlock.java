package com.github.commoble.magus.content;

import javax.annotation.Nullable;

import com.github.commoble.magus.BlockRegistrar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.BlockItemUseContext;
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

public class WizardGritBlock extends Block
{
	public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.REDSTONE_NORTH;
	public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.REDSTONE_EAST;
	public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.REDSTONE_SOUTH;
	public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.REDSTONE_WEST;

	public WizardGritBlock(Properties properties)
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, RedstoneSide.NONE).with(EAST, RedstoneSide.NONE).with(SOUTH, RedstoneSide.NONE)
			.with(WEST, RedstoneSide.NONE));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return Blocks.REDSTONE_WIRE.getShape(state, worldIn, pos, context);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		IBlockReader world = context.getWorld();
		BlockPos blockpos = context.getPos();
		return this.getDefaultState().with(WEST, this.getSide(world, blockpos, Direction.WEST)).with(EAST, this.getSide(world, blockpos, Direction.EAST))
			.with(NORTH, this.getSide(world, blockpos, Direction.NORTH)).with(SOUTH, this.getSide(world, blockpos, Direction.SOUTH));
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
	@Override	// from redstone
	public void updateDiagonalNeighbors(BlockState state, IWorld worldIn, BlockPos pos, int flags)
	{
		try (BlockPos.PooledMutable mutaPos = BlockPos.PooledMutable.retain())
		{
			for (Direction horizontalDir : Direction.Plane.HORIZONTAL)
			{
				RedstoneSide sideState = state.get(RedstoneWireBlock.FACING_PROPERTY_MAP.get(horizontalDir));
				if (sideState != RedstoneSide.NONE && worldIn.getBlockState(mutaPos.setPos(pos).move(horizontalDir)).getBlock() != this)
				{
					mutaPos.move(Direction.DOWN);
					BlockState stateBelowNeighbor = worldIn.getBlockState(mutaPos);
					//if (blockstate.getBlock() != Blocks.OBSERVER)
					//{
					BlockPos posBelowHere = mutaPos.offset(horizontalDir.getOpposite());
					BlockState newStateBelowNeighbor = stateBelowNeighbor.updatePostPlacement(horizontalDir.getOpposite(), worldIn.getBlockState(posBelowHere), worldIn, mutaPos,
						posBelowHere);
					replaceBlock(stateBelowNeighbor, newStateBelowNeighbor, worldIn, mutaPos, flags);
					//}

					mutaPos.setPos(pos).move(horizontalDir).move(Direction.UP);
					BlockState stateAboveNeighbor = worldIn.getBlockState(mutaPos);
//					if (stateAboveNeighbor.getBlock() != Blocks.OBSERVER)
//					{
					BlockPos stateAboveHere = mutaPos.offset(horizontalDir.getOpposite());
					BlockState newStateAboveNeighbor = stateAboveNeighbor.updatePostPlacement(horizontalDir.getOpposite(), worldIn.getBlockState(stateAboveHere), worldIn, mutaPos,
						stateAboveHere);
					replaceBlock(stateAboveNeighbor, newStateAboveNeighbor, worldIn, mutaPos, flags);
//					}
				}
			}
		}

	}

	// from redstone
	private RedstoneSide getSide(IBlockReader worldIn, BlockPos pos, Direction face)
	{
		BlockPos blockpos = pos.offset(face);
		BlockState blockstate = worldIn.getBlockState(blockpos);
		BlockPos blockpos1 = pos.up();
		BlockState blockstate1 = worldIn.getBlockState(blockpos1);
		if (!blockstate1.isNormalCube(worldIn, blockpos1))
		{
			boolean flag = blockstate.isSolidSide(worldIn, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
			if (flag && canConnectTo(worldIn.getBlockState(blockpos.up()), worldIn, blockpos.up(), null))
			{
				if (blockstate.isCollisionShapeOpaque(worldIn, blockpos))
				{
					return RedstoneSide.UP;
				}

				return RedstoneSide.SIDE;
			}
		}

		return !canConnectTo(blockstate, worldIn, blockpos, face)
			&& (blockstate.isNormalCube(worldIn, blockpos) || !canConnectTo(worldIn.getBlockState(blockpos.down()), worldIn, blockpos.down(), null)) ? RedstoneSide.NONE
				: RedstoneSide.SIDE;
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
	{
		BlockPos floorPos = pos.down();
		BlockState floorState = worldIn.getBlockState(floorPos);
		return floorState.isSolidSide(worldIn, floorPos, Direction.UP);
	}

	/**
	 * Calls World.notifyNeighborsOfStateChange() for all neighboring blocks, but
	 * only if the given block is the same block.
	 */
	private void notifySimilarNeighborsOfStateChange(World worldIn, BlockPos pos)
	{
		if (worldIn.getBlockState(pos).getBlock() == this)
		{
			worldIn.notifyNeighborsOfStateChange(pos, this);

			for (Direction direction : Direction.values())
			{
				worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
			}

		}
	}

	// from redstone
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		if (oldState.getBlock() != state.getBlock() && !worldIn.isRemote)
		{
			for (Direction direction : Direction.Plane.VERTICAL)
			{
				worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
			}

			for (Direction direction1 : Direction.Plane.HORIZONTAL)
			{
				this.notifySimilarNeighborsOfStateChange(worldIn, pos.offset(direction1));
			}

			for (Direction direction2 : Direction.Plane.HORIZONTAL)
			{
				BlockPos blockpos = pos.offset(direction2);
				if (worldIn.getBlockState(blockpos).isNormalCube(worldIn, blockpos))
				{
					this.notifySimilarNeighborsOfStateChange(worldIn, blockpos.up());
				}
				else
				{
					this.notifySimilarNeighborsOfStateChange(worldIn, blockpos.down());
				}
			}

		}
	}

	// from redstone
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (!isMoving && state.getBlock() != newState.getBlock())
		{
			super.onReplaced(state, worldIn, pos, newState, isMoving);
			if (!worldIn.isRemote)
			{
				for (Direction direction : Direction.values())
				{
					worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
				}

				for (Direction direction1 : Direction.Plane.HORIZONTAL)
				{
					this.notifySimilarNeighborsOfStateChange(worldIn, pos.offset(direction1));
				}

				for (Direction direction2 : Direction.Plane.HORIZONTAL)
				{
					BlockPos blockpos = pos.offset(direction2);
					if (worldIn.getBlockState(blockpos).isNormalCube(worldIn, blockpos))
					{
						this.notifySimilarNeighborsOfStateChange(worldIn, blockpos.up());
					}
					else
					{
						this.notifySimilarNeighborsOfStateChange(worldIn, blockpos.down());
					}
				}

			}
		}
	}
	
	public static void onDiagonalConnectorUpdate(Block sourceBlock, BlockPos sourcePos, World world)
	{
		for (Direction verticalDir : Direction.Plane.VERTICAL)
		{
			world.notifyNeighborsOfStateChange(sourcePos.offset(verticalDir), sourceBlock);
		}
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
	{
		if (!worldIn.isRemote)
		{
			if (state.isValidPosition(worldIn, pos))
			{
//				this.updateSurroundingRedstone(worldIn, pos, state);
			}
			else
			{
				spawnDrops(state, worldIn, pos);
				worldIn.removeBlock(pos, false);
			}

		}
	}

	protected static boolean canConnectTo(BlockState blockState, IBlockReader world, BlockPos pos, @Nullable Direction side)
	{
		return BlockRegistrar.WIZARD_GRIT_CONNECTORS.contains(blockState.getBlock());
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
		builder.add(NORTH, EAST, SOUTH, WEST);
	}
}
