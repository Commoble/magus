package com.github.commoble.magus;

import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;

public class BlockTagRegistrar
{
	/** tag for blocks that can diagonally connect to wizard grit dust **/
	public static final BlockTags.Wrapper WIZARD_GRIT_DIAGONAL_CONNECTORS = new BlockTags.Wrapper(new ResourceLocation(Magus.MODID, "wizard_grit_diagonal_connectors"));

}
