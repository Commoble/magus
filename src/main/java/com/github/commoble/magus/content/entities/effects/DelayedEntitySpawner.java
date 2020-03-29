package com.github.commoble.magus.content;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class DelayedEntitySpawner extends TemporaryEffectEntity
{
	public static final String SPAWN_TYPE = "spawn_type";
	public static final String DURATION = "duration";
	
	public EntityType<?> typeToSpawn = null;
	public int duration = 0;
	
	public DelayedEntitySpawner(EntityType<?> entityTypeIn, World worldIn)
	{
		super(entityTypeIn, worldIn);
	}
	
	public DelayedEntitySpawner(EntityType<?> type, World world, EntityType<?> toSpawn, int duration)
	{
		super(type, world);
		this.typeToSpawn = toSpawn;
		this.duration = duration;
	}

	@Override
	public int getMaxAge()
	{
		return this.duration;
	}

	@Override
	public boolean doTickBehaviorAndShouldContinue()
	{
		return true;
	}
	
	/** override this to perform an action just before the entity effect ends **/
	@Override
	public void onRemove()
	{
		Entity entity = this.typeToSpawn.create(this.world);
		entity.copyLocationAndAnglesFrom(this);
		this.world.addEntity(entity);
	}

	@Override
	protected void readAdditional(CompoundNBT compound)
	{
		super.read(compound);
		this.duration = compound.getInt(DURATION);
		String typeString = compound.getString(SPAWN_TYPE);
		if (typeString.length() > 0)
		{
			this.typeToSpawn = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(typeString));
		}
	}

	@Override
	protected void writeAdditional(CompoundNBT compound)
	{
		super.writeAdditional(compound);
		compound.putInt(DURATION, this.duration);
		if (this.typeToSpawn != null)
		{
			compound.putString(SPAWN_TYPE, this.typeToSpawn.getRegistryName().toString());
		}
	}
}
