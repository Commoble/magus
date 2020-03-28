package com.github.commoble.magus.content;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.commoble.magus.api.blocknetworks.ConnectionProvider;
import com.github.commoble.magus.util.DirectionUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagicCandleBlock extends TorchBlock implements ConnectionProvider
{
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final VoxelShape BASE_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 0.25D, 16);
	public static final VoxelShape SHAPE = VoxelShapes.or(TorchBlock.SHAPE, BASE_SHAPE);

	public MagicCandleBlock(Properties properties)
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(LIT, true));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(LIT);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if (state.has(LIT))
		{
			boolean wasLit = state.get(LIT);
			double x = pos.getX() + 0.5D;
			double y = pos.getY() + 0.5D;
			double z = pos.getZ() + 0.5D;
			Random rand = world.getRandom();
			
			if (world instanceof ServerWorld)
			{
				world.setBlockState(pos, state.with(LIT, !wasLit)); // toggle state
				if (wasLit)
				{
					((ServerWorld)world).spawnParticle(ParticleTypes.SMOKE, x, y, z, rand.nextInt(8)+4, 0.1, 0.1, 0.1, 0);
				}
			}

			world.playSound(player, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F);
			return ActionResultType.SUCCESS;
		}
		else
		{
			return ActionResultType.PASS;
		}
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

	@Override
	public Set<BlockPos> getPotentialConnections(IBlockReader world, BlockPos thisPos)
	{
		// return the north/south/east/west neighbors and down as well
		return DirectionUtil.HORIZONTALS_AND_DOWN.stream().map(dir -> thisPos.offset(dir)).collect(Collectors.toSet());
	}
}
