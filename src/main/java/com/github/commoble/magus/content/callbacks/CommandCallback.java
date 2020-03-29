package com.github.commoble.magus.content.callbacks;

import com.github.commoble.magus.Magus;
import com.github.commoble.magus.api.serializablefunctions.SerializableCallback;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CommandCallback extends SerializableCallback
{
	public static final String COMMAND = "command";
	
	private String unparsedCommand = "";
	
	public CommandCallback(ResourceLocation id, CompoundNBT extraData)
	{
		super(id, extraData);
	}

	@Override
	public CompoundNBT serializeExtraData()
	{
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString(COMMAND, this.unparsedCommand);
		return nbt;
	}

	@Override
	public void deserializeExtraData(CompoundNBT extraData)
	{
		this.unparsedCommand = extraData.getString(COMMAND);
	}

	@Override
	public void accept(World world, Vec3d pos)
	{
		if (world instanceof ServerWorld)
		{
			ServerWorld serverWorld = (ServerWorld) world;
			MinecraftServer server = serverWorld.getServer();
			CommandSource source = new CommandSource(
				ICommandSource.DUMMY,
				pos,			// world position, TODO
				Vec2f.ZERO,		// rotation, TODO
				serverWorld,
				2,				// permission level, TODO
				CallbackKeys.COMMAND_CALLBACK.toString(),
				new TranslationTextComponent(CallbackKeys.COMMAND_CALLBACK.toString()),
				server,
				null);
			try
			{
				server.getCommandManager().getDispatcher().execute(this.unparsedCommand, source);
			}
			catch(Exception exception)
			{
				Magus.LOGGER.error("Failed to parse command: " + this.unparsedCommand, exception);
			}
		}
	}

}
