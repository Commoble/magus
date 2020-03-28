package com.github.commoble.magus.client;

import com.github.commoble.magus.ConfigHelper;
import com.github.commoble.magus.ConfigHelper.ConfigValueListener;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig
{
	public final ConfigValueListener<Integer> BURNT_WIZARD_GRIT_TINT;
	
	public ClientConfig(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber)
	{
		builder.push("Block Tinting Colors");
		
			this.BURNT_WIZARD_GRIT_TINT = subscriber.subscribe(builder
				.comment("Burnt Wizard Grit Tint")
				.translation("magus.config.tint.burnt_wizard_grit")
				.defineInRange("burnt_wizard_grit", 0x44400b, 0x000000, 0xFFFFFF)
			);
		
		builder.pop();
	}
}
