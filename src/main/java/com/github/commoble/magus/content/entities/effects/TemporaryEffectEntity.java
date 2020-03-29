package com.github.commoble.magus.content.entities.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class TemporaryEffectEntity extends Entity
{
	public static final String AGE = "age";

	private int ticksExisted = 0;

	public TemporaryEffectEntity(EntityType<?> entityTypeIn, World worldIn)
	{
		super(entityTypeIn, worldIn);
	}

	@Override
	public void tick()
	{
		this.ticksExisted++;
		if (this.ticksExisted++ > this.getMaxAge() || !this.doTickBehaviorAndShouldContinue())
		{
			this.onEffectTimeout();
			this.remove();
		}
	}

	@Override
	protected int getPermissionLevel()
	{
		// return 0;
		return 2;	// set oplevel to 2 so we can run commands from this entity
	}

	public abstract int getMaxAge();

	public int getTicksExisted()
	{
		return this.ticksExisted;
	}

	/** override this to perform an action just before the entity effect ends **/
	public void onEffectTimeout()
	{

	}

	/** return false if entity should die immediately **/
	public abstract boolean doTickBehaviorAndShouldContinue();

	@Override
	protected void registerData()
	{

	}

	@Override
	protected void readAdditional(CompoundNBT compound)
	{
		this.ticksExisted = compound.getInt(AGE);
	}

	@Override
	protected void writeAdditional(CompoundNBT compound)
	{
		compound.putInt(AGE, this.ticksExisted);
	}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public static <E extends Entity> EntityType<E> getStandardEntityType(ResourceLocation id, IFactory<E> factory)
	{
		return EntityType.Builder.create(factory, EntityClassification.MISC).build(id.toString());
	}
}
