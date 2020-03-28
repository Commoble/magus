package com.github.commoble.magus.client;

import javax.annotation.Nullable;

import com.github.commoble.magus.Magus;
import com.github.commoble.magus.content.WizardGritBlock;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;

public class BlockColorHandlers
{
	public static final int NO_TINT = 0xFFFFFF; // white
	
	public static int getBurntWizardGritTint(BlockState state, @Nullable ILightReader reader, @Nullable BlockPos pos, int tintIndex)
	{
		if (tintIndex == 0 && state.has(WizardGritBlock.BURNT))
		{
			return state.get(WizardGritBlock.BURNT) ? Magus.clientConfig.BURNT_WIZARD_GRIT_TINT.get() : NO_TINT;
		}
		else
		{
			return NO_TINT;
		}
	}
}
