package com.artemka091102.pillaging_pillagers.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class NbtProvider implements ICapabilitySerializable<INBT>{
	@CapabilityInject(INbtCap.class)
    public static final Capability<INbtCap> CAPABILITY_NBT = null;

    final CompoundHandler myCapability = new CompoundHandler();
    private LazyOptional<INbtCap> instance = LazyOptional.of(CAPABILITY_NBT::getDefaultInstance);
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == CAPABILITY_NBT ? instance.cast() : LazyOptional.empty();
    }
    
    @Override
    public INBT serializeNBT() {
        return CAPABILITY_NBT.getStorage().writeNBT(CAPABILITY_NBT, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }
    
    @Override
    public void deserializeNBT(INBT nbt) {
        CAPABILITY_NBT.getStorage().readNBT(CAPABILITY_NBT, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}
