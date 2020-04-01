package com.github.commoble.magus.client;

import java.util.function.Supplier;

import com.github.commoble.magus.network.OpenLoreBookS2CPacket;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent;

/** Utility class for adding a degree of separation between packet classes and things that happen on the client only **/
public class ClientPacketHandlers
{
	public static void handlePacket(Supplier<NetworkEvent.Context> context, Runnable runnable)
	{
		context.get().enqueueWork(runnable);
		context.get().setPacketHandled(true);
	}
	
	public static void handleOpenLoreBookPacket(OpenLoreBookS2CPacket packet, Supplier<NetworkEvent.Context> context)
	{
		handlePacket(context, () ->
		{
			Minecraft.getInstance().displayGuiScreen(new LoreBookScreen(packet.getBookID()));
		});
	}
}
