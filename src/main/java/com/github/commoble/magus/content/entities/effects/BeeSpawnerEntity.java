package com.github.commoble.magus.content.entities.effects;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class BeeSpawnerEntity extends TemporaryEffectEntity
{
	public static final EntityPredicate BEE_TARGET_PREDICATE = new EntityPredicate().setCustomPredicate(target -> !(target instanceof BeeEntity));

	public BeeSpawnerEntity(EntityType<?> entityTypeIn, World worldIn)
	{
		super(entityTypeIn, worldIn);
	}

	@Override
	public int getMaxAge()
	{
		return 80;
	}

	@Override
	public boolean doTickBehaviorAndShouldContinue()
	{
		if (!this.world.isRemote())
		{
			if (this.getTicksExisted() % 2 == 0)
			{
				BeeEntity bee = EntityType.BEE.create(this.world);
				bee.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rand.nextFloat() * 360F, 0F);
				bee.setMotion(this.getRandomVelocity(0.1F), this.getRandomVelocity(0.1F), this.getRandomVelocity(0.1F));
				this.world.getTargettableEntitiesWithinAABB(LivingEntity.class, BEE_TARGET_PREDICATE, bee, bee.getBoundingBox().grow(10D)).stream()
					.reduce((targetA, targetB) -> targetB.getDistanceSq(this) < targetA.getDistance(this) ? targetB : targetA)
					.ifPresent(target -> bee.func_226391_a_(target));
				
				this.world.addEntity(bee);
				
			}
		}
		return true;
	}
	
	public float getRandomVelocity(float maxVelocity)
	{
		return (this.rand.nextFloat()*2F - 1F) * maxVelocity;
	}
}
