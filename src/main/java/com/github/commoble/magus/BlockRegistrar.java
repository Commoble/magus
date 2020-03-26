package com.github.commoble.magus;

import com.github.commoble.magus.content.MagicCandleBlock;
import com.github.commoble.magus.content.ObjectNames;
import com.github.commoble.magus.content.WizardGritBlock;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistrar
{
	private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Magus.MODID);

	public static void registerRegistry(IEventBus modBus)
	{
		BLOCKS.register(modBus);
	}

	public static final RegistryObject<WizardGritBlock> WIZARD_GRIT = BLOCKS.register(ObjectNames.WIZARD_GRIT, () ->
		new WizardGritBlock(Block.Properties.from(Blocks.REDSTONE_WIRE)));
	public static final RegistryObject<MagicCandleBlock> MAGIC_CANDLE = BLOCKS.register(ObjectNames.MAGIC_CANDLE, () ->
		new MagicCandleBlock(Block.Properties.from(Blocks.TORCH)));
	
	/** tag for blocks that can diagonally connect to wizard grit dust **/
	public static final BlockTags.Wrapper WIZARD_GRIT_DIAGONAL_CONNECTORS = new BlockTags.Wrapper(new ResourceLocation(Magus.MODID, "wizard_grit_diagonal_connectors"));
}
