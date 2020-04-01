package com.github.commoble.magus.util;

import java.util.List;
import java.util.function.BiConsumer;

public class ListUtil
{
	/** Runs the given index-aware consumer on each item in the list. Use list.foreach() instead if the items' indices are not needed. **/ 
	public static <T> void forEachIndex(List<T> list, BiConsumer<T, Integer> indexer)
	{
		int size = list.size();
		for (int i=0; i<size; i++)
		{
			indexer.accept(list.get(i), i);
		}
	}
}
