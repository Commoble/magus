package com.github.commoble.magus.api.serializablefunctions;

import com.github.commoble.magus.Magus;

import net.minecraft.util.ResourceLocation;

/** keys for CallbackRegistries.ENTITY_CALLBACKS //todo setup some kind of deferred-registry-like system? **/
public class CallbackKeys
{
	public static final ResourceLocation BEE_SWARM = new ResourceLocation(Magus.MODID, "bee_swarm");
	public static final ResourceLocation SPAWN_ENTITY = new ResourceLocation(Magus.MODID, "spawn_entity");
}
