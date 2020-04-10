package com.artemka091102.pillaging_pillagers.capabilities;

import net.minecraft.nbt.CompoundNBT;

public interface INbtCap {
	CompoundNBT getCompound();
	void setCompound(CompoundNBT nbt);
}