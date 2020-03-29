package com.github.commoble.magus.content.callbacks;

import com.github.commoble.magus.Magus;

import net.minecraft.util.ResourceLocation;

/** keys for CallbackRegistries.ENTITY_CALLBACKS //todo setup some kind of deferred-registry-like system? **/
public class CallbackKeys
{
	public static final ResourceLocation BEE_SWARM = new ResourceLocation(Magus.MODID, "bee_swarm");
	public static final ResourceLocation SPAWN_ENTITY = new ResourceLocation(Magus.MODID, "spawn_entity");
	public static final ResourceLocation COMPOSITE_CALLBACK = new ResourceLocation(Magus.MODID, "composite_callback");
	public static final ResourceLocation COMMAND_CALLBACK = new ResourceLocation(Magus.MODID, "command_callback");
}
