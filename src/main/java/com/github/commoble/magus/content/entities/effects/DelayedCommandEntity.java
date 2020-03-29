package com.github.commoble.magus.content.entities.effects;

import com.github.commoble.magus.EntityTypeRegistrar;
import com.github.commoble.magus.Magus;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DelayedCommandEntity extends TemporaryEffectEntity
{
	public static final String COMMAND = "command";
	public static final String DURATION = "duration";

	private int duration = 0;
	private String unparsedCommand = "";
	
	public DelayedCommandEntity(EntityType<?> entityTypeIn, World worldIn)
	{
		super(entityTypeIn, worldIn);
	}
	
	public DelayedCommandEntity(EntityType<?> type, World world, int duration, String unparsedCommand)
	{
		super(type, world);
		this.duration = duration;
		this.unparsedCommand = unparsedCommand;
	}
	
	public static DelayedCommandEntity spawn(World world, Vec3d pos, int duration, String unparsedCommand)
	{
		DelayedCommandEntity entity = new DelayedCommandEntity(EntityTypeRegistrar.DELAYED_COMMAND_ENTITY.get(), world, duration, unparsedCommand);
		entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
		world.addEntity(entity);
		return entity;
	}
	
	/** override this to perform an action just before the entity effect ends **/
	@Override
	public void onEffectTimeout()
	{
		if (this.world instanceof ServerWorld)
		{
			try
			{
				this.world.getServer().getCommandManager().getDispatcher().execute(this.unparsedCommand, this.getCommandSource().withFeedbackDisabled());
			}
			catch(Exception exception)
			{	
				Magus.LOGGER.error("Failed to parse command: " + this.unparsedCommand, exception);
			}
		}
	}

	@Override
	protected void readAdditional(CompoundNBT compound)
	{
		super.readAdditional(compound);
		this.duration = compound.getInt(DURATION);
		this.unparsedCommand = compound.getString(COMMAND);
	}

	@Override
	protected void writeAdditional(CompoundNBT compound)
	{
		super.writeAdditional(compound);
		compound.putInt(DURATION, this.duration);
		compound.putString(COMMAND, this.unparsedCommand);
	}

	@Override
	public int getMaxAge()
	{
		return this.duration;
	}

	@Override
	public boolean doTickBehaviorAndShouldContinue()
	{
		return true;
	}

}
