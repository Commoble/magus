package com.github.commoble.magus;

import com.github.commoble.magus.content.BeeSpawnerEntity;
import com.github.commoble.magus.content.DelayedEntitySpawner;
import com.github.commoble.magus.content.ObjectNames;
import com.github.commoble.magus.content.TemporaryEffectEntity;
import com.github.commoble.magus.content.UnrelentingCubeEntity;

import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypeRegistrar
{
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, Magus.MODID);
	
	public static void registerRegistry(IEventBus modBus)
	{
		ENTITY_TYPES.register(modBus);
	}
	
	public static final RegistryObject<EntityType<UnrelentingCubeEntity>> UNRELENTING_CUBE = ENTITY_TYPES.register(
		ObjectNames.UNRELENTING_CUBE, ()-> UnrelentingCubeEntity.getStandardEntityType());
	public static final RegistryObject<EntityType<BeeSpawnerEntity>> BEE_SPAWNER = ENTITY_TYPES.register(ObjectNames.BEE_SPAWNER,
		() -> TemporaryEffectEntity.getStandardEntityType(new ResourceLocation(Magus.MODID, ObjectNames.BEE_SPAWNER), BeeSpawnerEntity::new));
	public static final RegistryObject<EntityType<DelayedEntitySpawner>> DELAYED_ENTITY_SPAWNER = ENTITY_TYPES.register(ObjectNames.DELAYED_ENTITY_SPAWNER,
		() -> TemporaryEffectEntity.getStandardEntityType(new ResourceLocation(Magus.MODID, ObjectNames.DELAYED_ENTITY_SPAWNER), DelayedEntitySpawner::new));
}
