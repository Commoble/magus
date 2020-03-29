package com.github.commoble.magus.content.entities;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * as DamagingProjectileEntity, but it only collides with entities and not
 * blocks the DamagingProjectileEntity does a raytrace against blocks in all
 * cases, so we need to override the tick method in a new class to prevent that
 */
public abstract class IntangibleProjectileEntity extends DamagingProjectileEntity
{
	protected IntangibleProjectileEntity(EntityType<? extends IntangibleProjectileEntity> type, World world)
	{
		super(type, world);
	}

	// maximum lifetime in ticks
	public abstract int getMaxDurationInTicks(); // we also want to kill this after a while to make sure we don't leave
													// projectiles lying around

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick()
	{
		if (this.world.isRemote
			|| (this.shootingEntity == null || !this.shootingEntity.removed) && this.ticksExisted < this.getMaxDurationInTicks() && this.world.isBlockLoaded(new BlockPos(this)))
		{
			RayTraceResult raytraceresult = raytraceEntities(this, this.shootingEntity);
			if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS
				&& !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult))
			{
				this.onImpact(raytraceresult);
			}

			Vec3d acceleration = this.getMotion();
			double ax = this.getPosX() + acceleration.x;
			double ay = this.getPosY() + acceleration.y;
			double az = this.getPosZ() + acceleration.z;
			ProjectileHelper.rotateTowardsMovement(this, 0.2F);
			float speed = this.getMotionFactor();

			this.setMotion(acceleration.add(this.accelerationX, this.accelerationY, this.accelerationZ).scale(speed));
			this.world.addParticle(this.getParticle(), ax, ay + 0.5D, az, 0.0D, 0.0D, 0.0D);
			this.setPosition(ax, ay, az);
		}
		else
		{
			this.remove();
		}
	}

	public static RayTraceResult raytraceEntities(Entity projectile, @Nullable Entity shooter)
	{
		Predicate<Entity> filter = entity -> canHitEntity(shooter, entity);
		AxisAlignedBB currentAABB = projectile.getBoundingBox();
		Vec3d motion = projectile.getMotion();
		AxisAlignedBB strikeAABB = currentAABB.offset(motion);
		AxisAlignedBB searchAABB = strikeAABB.grow(1D);
		World world = projectile.world;
		Vec3d currentPosition = projectile.getPositionVec();
		Vec3d nextPosition = currentPosition.add(motion);

		double nearestTargetDistance = Double.MAX_VALUE;
		Entity nearestTarget = null;

		for (Entity target : world.getEntitiesInAABBexcluding(projectile, searchAABB, filter))
		{
			AxisAlignedBB targetAABB = target.getBoundingBox().grow(0.3F);
			if (targetAABB.intersects(strikeAABB))
			{
				double targetDistance = nextPosition.squareDistanceTo(target.getPositionVec());
				if (targetDistance < nearestTargetDistance)
				{
					nearestTarget = target;
					nearestTargetDistance = targetDistance;
				}
			}
		}

		return nearestTarget == null ? null : new EntityRayTraceResult(nearestTarget);
	}

	public static boolean canHitEntity(Entity shooter, Entity target)
	{
		return !target.isSpectator() && target.canBeCollidedWith() && !target.isEntityEqual(shooter) && !target.noClip;
	}
}
