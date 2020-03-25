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
		this.setDefaultState(
			this.stateContainer.getBaseState().with(NORTH, RedstoneSide.NONE).with(EAST, RedstoneSide.NONE).with(SOUTH, RedstoneSide.NONE).with(WEST, RedstoneSide.NONE));
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
	@Override
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
					if (BlockRegistrar.WIZARD_GRIT_CONNECTORS.contains(oldDiagonalState.getBlock()))
					{
						BlockPos targetPos = mutablePos.toImmutable();
						Direction faceDirection = horizontalDirection.getOpposite();
						BlockPos facePos = targetPos.offset(faceDirection);
						// update the wizard grit as if updated by the block just above or below the caller
						BlockState newDiagonalState = oldDiagonalState.updatePostPlacement(faceDirection, world.getBlockState(facePos), world, targetPos, facePos);
						replaceBlock(oldDiagonalState, newDiagonalState, world, targetPos, flags);
					}
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
