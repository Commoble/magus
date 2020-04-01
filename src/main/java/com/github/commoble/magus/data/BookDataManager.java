package com.github.commoble.magus.data;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class BookDataManager extends JsonReloadListener
{
	public static final Gson GSON = new Gson();
	
	public BookDataManager()
	{
		super(GSON, "magus:lore_books");
	}

	public BookDataManager(Gson gson, String s)
	{
		super(gson, s);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonObject> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
	{
		// TODO Auto-generated method stub
		
	}

}
