package com.artemka091102.pillaging_pillagers.capabilities;

import net.minecraft.nbt.CompoundNBT;

public class CompoundHandler implements INbtCap {
	private CompoundNBT nbt;

    public CompoundHandler() {
        this.nbt = new CompoundNBT();
    }

    @Override
    public CompoundNBT getCompound() {
        return this.nbt;
    }

    @Override
    public void setCompound(CompoundNBT nbt) {
        this.nbt = nbt;
    }
}
