package com.github.commoble.magus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.commoble.magus.MixinHooks;

import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

@Mixin(BellTileEntity.class)
public abstract class BellTileEntityMixin extends TileEntity
{
	private BellTileEntityMixin(TileEntityType<?> tileEntityTypeIn)
	{
		super(tileEntityTypeIn);
	}

	@Inject(at = @At("HEAD"), method = "ring")
	public void onBellRung(CallbackInfo info)
	{
		MixinHooks.onBellRung(info, this.getWorld(), this.getPos());
	}
}
