package com.artemka091102.test_mod.capabilities;

import net.minecraft.nbt.CompoundNBT;

public interface INbtCap {
	CompoundNBT getCompound();
	void setCompound(CompoundNBT nbt);
}