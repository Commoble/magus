package com.github.commoble.magus.content;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagicCandleBlock extends TorchBlock
{
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final VoxelShape BASE_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 0.25D, 16);
	public static final VoxelShape SHAPE = VoxelShapes.or(TorchBlock.SHAPE, BASE_SHAPE);

	public MagicCandleBlock(Properties properties)
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(LIT, true).with(POWERED, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(LIT, POWERED);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}

	/**
	 * Amount of light emitted
	 * 
	 * @deprecated prefer calling {@link BlockState#getLightValue()}
	 */
	@Deprecated
	@Override
	public int getLightValue(BlockState state)
	{
		return state.get(LIT) ? super.getLightValue(state) : 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		if (stateIn.has(LIT) && stateIn.get(LIT) == true)
		{
			super.animateTick(stateIn, worldIn, pos, rand);
		}
	}
}
