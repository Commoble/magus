package com.github.commoble.magus.content;

import com.github.commoble.magus.api.serializablefunctions.CallbackRegistries;
import com.github.commoble.magus.api.serializablefunctions.NoCallback;
import com.github.commoble.magus.api.serializablefunctions.SerializableCallback;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class DelayedCallbackEntity extends TemporaryEffectEntity
{
	public static final SerializableCallback EMPTY_CALLBACK = NoCallback.getEmptyCallback();
	public static final String DURATION = "duration";
	public static final String CALLBACK = "callback";
	
	private int duration = 0;
	private SerializableCallback callback = EMPTY_CALLBACK;

	public DelayedCallbackEntity(EntityType<?> entityTypeIn, World worldIn)
	{
		super(entityTypeIn, worldIn);
	}
	
	public DelayedCallbackEntity(EntityType<?> type, World world, int maxDuration, ResourceLocation callbackKey, CompoundNBT extraData)
	{
		super(type, world);
		this.duration = maxDuration;
		this.callback = CallbackRegistries.CALLBACKS.createCallback(callbackKey, extraData);
	}

	@Override
	public int getMaxAge()
	{
		return this.duration;
	}

	@Override
	public boolean doTickBehaviorAndShouldContinue()
	{
		this.callback.accept(this.world, this.getPosition());
		return true;
	}

	@Override
	protected void readAdditional(CompoundNBT compound)
	{
		super.readAdditional(compound);
		this.duration = compound.getInt(DURATION);
		this.callback = CallbackRegistries.CALLBACKS.deserialize(compound.getCompound(CALLBACK));
	}

	@Override
	protected void writeAdditional(CompoundNBT compound)
	{
		super.writeAdditional(compound);
		compound.putInt(DURATION, this.duration);
		compound.put(CALLBACK, this.callback.serialize());
		
	}
}
