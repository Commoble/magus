package com.github.commoble.magus.api.serializablefunctions;

import java.util.function.Predicate;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/** Interface for storing predicates in entities n' things **/
public interface SerializablePredicate<T> extends Predicate<T>, INBTSerializable<CompoundNBT>
{

}
