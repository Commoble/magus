package com.github.commoble.magus.content.callbacks;

import java.util.ArrayList;
import java.util.List;

import com.github.commoble.magus.api.serializablefunctions.CallbackUtil;
import com.github.commoble.magus.api.serializablefunctions.SerializableCallback;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CompositeCallback extends SerializableCallback
{
	public static final String CALLBACKS = "callbacks";
	
	private List<SerializableCallback> callbacks = new ArrayList<>();
	
	public CompositeCallback(ResourceLocation id, CompoundNBT extraData)
	{
		super(id, extraData);
	}

	@Override
	public CompoundNBT serializeExtraData()
	{
		CompoundNBT out = new CompoundNBT();
		ListNBT list = new ListNBT();
		for (SerializableCallback callback : this.callbacks)
		{
			list.add(callback.serialize());
		}
		out.put(CALLBACKS, list);
		return out;
	}

	@Override
	public void deserializeExtraData(CompoundNBT extraData)
	{
		ListNBT list = extraData.getList(CALLBACKS, 0);
		ArrayList<SerializableCallback> newList = new ArrayList<>();
		list.forEach(nbt -> newList.add(CallbackUtil.deserialize((CompoundNBT)nbt)));
		this.callbacks = newList;
	}

	@Override
	public void accept(World world, Vec3d pos)
	{
		this.callbacks.forEach(callback -> callback.accept(world, pos));
	}

}
