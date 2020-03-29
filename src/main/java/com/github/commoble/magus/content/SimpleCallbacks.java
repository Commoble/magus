package com.github.commoble.magus.content;

import com.github.commoble.magus.EntityTypeRegistrar;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/** Defines the miscellanous SimpleCallbacks registered to the callback registry**/
public class SimpleCallbacks
{
	public static void ejectBeesFromEntity(World world, Vec3d pos)
	{
		if (world instanceof ServerWorld)
		{
			Entity beeSpawner = EntityTypeRegistrar.BEE_SPAWNER.get().create(world);
			beeSpawner.setPosition(pos.getX(), pos.getY(), pos.getZ());
			world.addEntity(beeSpawner);
		}
	}
}
