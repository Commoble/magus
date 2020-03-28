package com.github.commoble.magus.api.serializablefunctions;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class SpawnEntityCallback extends SerializableCallback
{
	public static final String ENTITY_TYPE = "entity_type";
	public static final String ENTITY_DATA = "entity_data";

	protected EntityType<?> typeToSpawn = null;
	protected CompoundNBT entityData = new CompoundNBT();
	
	public SpawnEntityCallback(ResourceLocation id, CompoundNBT extraData)
	{
		super(id, extraData);
	}
	
	public static CompoundNBT getSpawnData(EntityType<?> type, CompoundNBT entityData)
	{
		CompoundNBT nbt = new CompoundNBT();
		if (type != null)
		{
			nbt.putString(ENTITY_TYPE, type.getRegistryName().toString());
		}
		nbt.put(ENTITY_DATA, entityData);
		return nbt;
	}

	@Override
	public CompoundNBT serializeExtraData()
	{
		return getSpawnData(this.typeToSpawn, this.entityData);
	}

	@Override
	public void deserializeExtraData(CompoundNBT extraData)
	{
		String type = extraData.getString(ENTITY_TYPE);
		if (type.length() > 0)
		{
			this.typeToSpawn = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(type));
		}
		this.entityData = extraData.getCompound(ENTITY_DATA);
	}

	@Override
	public void accept(World world, BlockPos pos)
	{
		this.typeToSpawn.spawn(world, this.entityData, null, null, pos, SpawnReason.EVENT, false, false);
	}

}
