package com.github.commoble.magus.client;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.commoble.magus.BlockRegistrar;
import com.github.commoble.magus.EntityTypeRegistrar;
import com.github.commoble.magus.content.WizardGritBlock;

import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler
{
	public static void subscribeClientEvents(IEventBus modBus, IEventBus forgeBus)
	{
		modBus.addListener(ClientEventHandler::onRegisterBlockColors);
		modBus.addListener(ClientEventHandler::onBakeModels);
		modBus.addListener(ClientEventHandler::onClientSetup);
	}

	private static void onRegisterBlockColors(ColorHandlerEvent.Block event)
	{
		event.getBlockColors().register(BlockColorHandlers::getBurntWizardGritTint, BlockRegistrar.WIZARD_GRIT.get());
	}

	private static void onBakeModels(ModelBakeEvent event)
	{
		// we want to replace some of the regular baked block models with models that
		// have emissive/fullbright textures
		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();

		// the model registry uses ModelResourceLocations that can't easily be compared
		// to regular resource locations
		// they have an additional field for the blockstate properties of a blockstate
		// so we need to replace models on a per-blockstate bases
		
		// we need to use existing models to create our enhanced models, so we'll need to make sure they're in the registry first and get them
		// let's make a reusable model override function
		// the resourcelocations we specify in the FullbrightBakedModel constructor are *texture* locations
		Consumer<ModelResourceLocation> overrideWizardGritModel = getModelOverrider(modelRegistry, baseModel ->
			new FullbrightBakedModel(baseModel,
				 new ResourceLocation("magus:block/wizard_grit_line0"),
				 new ResourceLocation("magus:block/wizard_grit_line1"),
				 new ResourceLocation("magus:block/wizard_grit_dot")));
		
		
		// now we get all the blockstates from our block, narrow them down to the only ones we want to have fullbright textures,
		// and replace the models with fullbright-enabled models
		BlockRegistrar.WIZARD_GRIT.get().getStateContainer().getValidStates().stream()
			.filter(state -> !state.get(WizardGritBlock.BURNT))
			.map(BlockModelShapes::getModelLocation)
			.forEach(overrideWizardGritModel);
	}

	public static Consumer<ModelResourceLocation> getModelOverrider(Map<ResourceLocation, IBakedModel> registry, Function<IBakedModel, IBakedModel> modelFunction)
	{
		return key ->
		{
			if (registry.containsKey(key))
			{
				registry.put(key, modelFunction.apply(registry.get(key)));
			}
		};
	}

	private static void onClientSetup(FMLClientSetupEvent event)
	{
		RenderTypeLookup.setRenderLayer(BlockRegistrar.MAGIC_CANDLE.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockRegistrar.WIZARD_GRIT.get(), RenderType.getCutout());
		RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistrar.UNRELENTING_CUBE.get(), UnrelentingCubeEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistrar.BEE_SPAWNER.get(), InvisibleEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegistrar.DELAYED_COMMAND_ENTITY.get(), InvisibleEntityRenderer::new);
	}
}
