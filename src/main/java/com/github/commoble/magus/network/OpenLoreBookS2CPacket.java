package com.github.commoble.magus.network;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.github.commoble.magus.client.ClientPacketHandlers;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class OpenLoreBookS2CPacket
{
	@Nonnull private final ResourceLocation bookID;

	// called to construct packet by sender
	public OpenLoreBookS2CPacket(ResourceLocation bookID)
	{
		this.bookID = bookID;
	}
	
	// called to construct packet by receiver
	public OpenLoreBookS2CPacket(PacketBuffer buffer)
	{
		this.bookID = new ResourceLocation(buffer.readString());
	}
	
	public ResourceLocation getBookID()
	{
		return this.bookID;
	}

	// called to prepare packet by sender
	public void write(PacketBuffer buffer)
	{
		buffer.writeString(this.bookID.toString());
	}

	// called when receiver receives packet
	public void handle(Supplier<Context> context)
	{
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleOpenLoreBookPacket(this, context));
	}

}
