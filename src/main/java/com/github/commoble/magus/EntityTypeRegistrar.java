package com.github.commoble.magus;

import com.github.commoble.magus.content.ObjectNames;
import com.github.commoble.magus.content.entities.UnrelentingCubeEntity;
import com.github.commoble.magus.content.entities.effects.BeeSpawnerEntity;
import com.github.commoble.magus.content.entities.effects.DelayedCommandEntity;
import com.github.commoble.magus.content.entities.effects.TemporaryEffectEntity;

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
//	public static final RegistryObject<EntityType<DelayedEntitySpawner>> DELAYED_ENTITY_SPAWNER = ENTITY_TYPES.register(ObjectNames.DELAYED_ENTITY_SPAWNER,
//		() -> TemporaryEffectEntity.getStandardEntityType(new ResourceLocation(Magus.MODID, ObjectNames.DELAYED_ENTITY_SPAWNER), DelayedEntitySpawner::new));
//	public static final RegistryObject<EntityType<DelayedCallbackEntity>> DELAYED_CALLBACK_ENTITY = ENTITY_TYPES.register(ObjectNames.DELAYED_CALLBACK_EFFECT,
//		() -> TemporaryEffectEntity.getStandardEntityType(new ResourceLocation(Magus.MODID, ObjectNames.DELAYED_CALLBACK_EFFECT), DelayedCallbackEntity::new));
	public static final RegistryObject<EntityType<DelayedCommandEntity>> DELAYED_COMMAND_ENTITY = ENTITY_TYPES.register(ObjectNames.DELAYED_COMMAND,
		() -> TemporaryEffectEntity.getStandardEntityType(new ResourceLocation(Magus.MODID, ObjectNames.DELAYED_COMMAND), DelayedCommandEntity::new));
}
