package com.github.commoble.magus;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MixinHooks
{
	public static void onBellRung(CallbackInfo info, World world, BlockPos pos)
	{
	}
}
