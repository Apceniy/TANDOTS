package com.artemka091102.test_mod.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class Storage implements IStorage<INbtCap> {
	public INBT writeNBT(Capability<INbtCap> capability, INbtCap instance, Direction side) {
        final CompoundNBT tag = new CompoundNBT();          
        return tag.merge(instance.getCompound());
    }

    public void readNBT(Capability<INbtCap> capability, INbtCap instance, Direction side, INBT nbt) {
        final CompoundNBT tag = (CompoundNBT) nbt;
        instance.setCompound(tag);
    }
}