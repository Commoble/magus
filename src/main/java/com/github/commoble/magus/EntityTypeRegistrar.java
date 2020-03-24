package com.github.commoble.magus;

import com.github.commoble.magus.content.ObjectNames;
import com.github.commoble.magus.content.UnrelentingCubeEntity;

import net.minecraft.entity.EntityType;
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
}
