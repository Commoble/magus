package com.github.commoble.magus;

import java.util.function.Consumer;

import com.github.commoble.magus.client.ClientConfig;
import com.github.commoble.magus.client.ClientEventHandler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Magus.MODID)
public class Magus
{
	public static final String MODID = "magus";
	
	public static ClientConfig clientConfig;
	
	public Magus()
	{
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		
		subscribeDeferredRegisters(modBus,
			BlockRegistrar::registerRegistry,
			ItemRegistrar::registerRegistry,
			EntityTypeRegistrar::registerRegistry);
		
		modBus.addListener(CommonModEvents::onCommonSetup);
		
		clientConfig = ConfigHelper.register(ModConfig.Type.CLIENT, ClientConfig::new);
		
		// use a layer of indirection when subscribing client events to avoid classloading client classes on server
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ClientEventHandler.subscribeClientEvents(modBus, forgeBus));
	}
	
	@SafeVarargs
	public static void subscribeDeferredRegisters(IEventBus modBus, Consumer<IEventBus>... subscribers)
	{
		for (Consumer<IEventBus> subscriber : subscribers)
		{
			subscriber.accept(modBus);
		}
	}
}
