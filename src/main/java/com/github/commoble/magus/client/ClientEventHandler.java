package com.github.commoble.magus.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.github.commoble.magus.BlockRegistrar;
import com.github.commoble.magus.EntityTypeRegistrar;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler
{
	public static void subscribeClientEvents(IEventBus modBus, IEventBus forgeBus)
	{
		modBus.addListener(ClientEventHandler::onBakeModels);
		modBus.addListener(ClientEventHandler::onClientSetup);
	}

	private static void onBakeModels(ModelBakeEvent event)
	{
		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		
		// the model registry uses ModelResourceLocations that can't easily be compared to regular resource locations
		// in particular, given a set of MRLs, set.contains(regular RL) will always return false
		// so we have to do some stupid things to get this to work nicely
		
		Map<ResourceLocation, BiFunction<Map<ResourceLocation, IBakedModel>, IBakedModel, IBakedModel>> modelOverrideFactories = new HashMap<>();
		
		modelOverrideFactories.put(BlockRegistrar.WIZARD_GRIT.getId(),
			(registry, baseModel) -> new FullbrightBakedModel(baseModel,
				new ResourceLocation("magus:block/wizard_grit_line0"),
				new ResourceLocation("magus:block/wizard_grit_line1"),
				new ResourceLocation("magus:block/wizard_grit_dot")));
		
		for (ResourceLocation existingLocation : modelRegistry.keySet())
		{
			BiFunction<Map<ResourceLocation, IBakedModel>, IBakedModel, IBakedModel> overrideFactory =
				modelOverrideFactories.get(new ResourceLocation(existingLocation.getNamespace(), existingLocation.getPath()));
			
			if (overrideFactory != null)
			{
				modelRegistry.put(existingLocation, overrideFactory.apply(modelRegistry, modelRegistry.get(existingLocation)));
			}
		}
	}

	private static void onClientSetup(FMLClientSetupEvent event)
	{
		RenderTypeLookup.setRenderLayer(BlockRegistrar.MAGIC_CANDLE.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockRegistrar.WIZARD_GRIT.get(), RenderType.getCutout());
		RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistrar.UNRELENTING_CUBE.get(), UnrelentingCubeEntityRenderer::new);
	}
}
