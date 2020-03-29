package com.github.commoble.magus.content.entities;

import com.github.commoble.magus.EntityTypeRegistrar;
import com.github.commoble.magus.content.ObjectNames;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity;
import net.minecraftforge.fml.network.NetworkHooks;

public class UnrelentingCubeEntity extends IntangibleProjectileEntity
{

	public static final float BASE_DAMAGE = 6.0F;
	public static final int DURATION_IN_TICKS = 20*8;	// 8 seconds
	public static final float SIZE = 10F;
	
	protected int timeAlive = 0;

	public UnrelentingCubeEntity(EntityType<? extends UnrelentingCubeEntity> entityTypeIn, World worldIn)
	{
		super(entityTypeIn, worldIn);
	}

	public UnrelentingCubeEntity(LivingEntity shooter, World world, double ax, double ay, double az)
	{
		super(EntityTypeRegistrar.UNRELENTING_CUBE.get(), world);
		this.shootingEntity = shooter;
		this.setLocationAndAngles(shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), shooter.rotationYaw, shooter.rotationPitch);
		this.recenterBoundingBox();
		this.setMotion(Vec3d.ZERO);
		double d0 = MathHelper.sqrt(ax * ax + ay * ay + az * az);
		this.accelerationX = ax / d0 * 0.1D;
		this.accelerationY = ay / d0 * 0.1D;
		this.accelerationZ = az / d0 * 0.1D;

	}

	public static UnrelentingCubeEntity spawnOnClient(SpawnEntity spawnPacket, World world)
	{
		return new UnrelentingCubeEntity(EntityTypeRegistrar.UNRELENTING_CUBE.get(), world);
	}

	public static EntityType<UnrelentingCubeEntity> getStandardEntityType()
	{
		// we declare the factory in a local variable to help type inference
		EntityType.IFactory<UnrelentingCubeEntity> factory = UnrelentingCubeEntity::new;
		return EntityType.Builder.create(factory, EntityClassification.MISC)
			.immuneToFire()
			.size(SIZE, SIZE)
			.build(ObjectNames.UNRELENTING_CUBE);
	}

	public static void launchAsProjectile(LivingEntity shooter, World world)
	{
		Vec3d lookVec = shooter.getLookVec();
		UnrelentingCubeEntity projectile = new UnrelentingCubeEntity(shooter, world, lookVec.x, lookVec.y, lookVec.z);

		// from arrow
		projectile.setRawPosition(shooter.getPosX(), shooter.getPosYEye() - 0.1F, shooter.getPosZ());
		world.addEntity(projectile);
	}

	/**
	 * Called when this entity hits a block or entity.
	 */
	@Override
	protected void onImpact(RayTraceResult result)
	{
		if (result.getType() == RayTraceResult.Type.ENTITY)
		{
			// the projectile collision raytracer ignores the projectile's shooter so we
			// don't need to check for it here
			Entity entity = ((EntityRayTraceResult) result).getEntity();
			// hurt collided entity, mark the shooter as the source of the damage, allow
			// projectile resistance and thorns to apply
			entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.shootingEntity).setProjectile(), BASE_DAMAGE);
			this.applyEnchantments(this.shootingEntity, entity);
		}

	}

	@Override
	public int getMaxDurationInTicks()
	{
		return 40;//DURATION_IN_TICKS;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this
	 * Entity.
	 */
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	/**
	 * Called when the entity is attacked.
	 */
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		return false;
	}

	@Override
	protected boolean isFireballFiery()
	{
		return false;
	}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
