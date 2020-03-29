package com.github.commoble.magus;

import com.github.commoble.magus.api.CachedEntry;
import com.github.commoble.magus.api.serializablefunctions.CallbackFactory;
import com.github.commoble.magus.api.serializablefunctions.CallbackUtil;
import com.github.commoble.magus.content.SimpleCallbacks;
import com.github.commoble.magus.content.callbacks.CallbackKeys;
import com.github.commoble.magus.content.callbacks.CommandCallback;
import com.github.commoble.magus.content.callbacks.SpawnEntityCallback;

// deferred registries don't work with custom forge registries, have to register these the old-fashioned way
public class CallbackRegistrar
{
	public static CachedEntry<CallbackFactory> BEE_SWARM;
	public static CachedEntry<CallbackFactory> SPAWN_ENTITY;
	public static CachedEntry<CallbackFactory> COMMAND;
	
	public static void registerCallbacks()
	{
		BEE_SWARM = CallbackUtil.registerSimpleCallback(CallbackKeys.BEE_SWARM, SimpleCallbacks::ejectBeesFromEntity);
		SPAWN_ENTITY = CallbackUtil.registerCallback(CallbackKeys.SPAWN_ENTITY, SpawnEntityCallback::new);
		COMMAND = CallbackUtil.registerCallback(CallbackKeys.COMMAND_CALLBACK, CommandCallback::new);
	}
}
