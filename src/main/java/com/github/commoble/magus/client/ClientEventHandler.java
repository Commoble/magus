package com.github.commoble.magus.client;

import com.github.commoble.magus.BlockRegistrar;
import com.github.commoble.magus.EntityTypeRegistrar;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler
{
	public static void subscribeClientEvents(IEventBus modBus, IEventBus forgeBus)
	{
		modBus.addListener(ClientEventHandler::onClientSetup);
	}
	
	private static void onClientSetup(FMLClientSetupEvent event)
	{
		RenderTypeLookup.setRenderLayer(BlockRegistrar.MAGIC_CANDLE.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockRegistrar.WIZARD_GRIT.get(), RenderType.getCutout());
		RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistrar.UNRELENTING_CUBE.get(), UnrelentingCubeEntityRenderer::new);
	}
}
