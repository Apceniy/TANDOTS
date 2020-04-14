package com.artemka091102.pillaging_pillagers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PillagingPillagers.MODID)

public class PillagingPillagers {
	
	public static final String MODID = "pillaging_pillagers";
	
	public PillagingPillagers() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(EventsHandler::onCommonSetup);
	}
	
	// public static final List<World> LOADED_WORLDS = new ArrayList<>();
	
}