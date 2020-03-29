package com.github.commoble.magus;

import java.util.stream.IntStream;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.commoble.magus.content.entities.effects.DelayedCommandEntity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MixinHooks
{
	public static void onBellRung(CallbackInfo info, World world, BlockPos pos)
	{
		IntStream.range(0, 60).forEach(i ->	DelayedCommandEntity.spawn(world, new Vec3d(pos).add(i,60,0), 20*i, "summon minecraft:pig"));
	}
}
