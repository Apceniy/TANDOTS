package com.artemka091102.pillaging_pillagers;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PillagingPillagers.MODID)

public class PillagingPillagers {
	public PillagingPillagers() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(EventsHandler::onCommonSetup);
	}
	
	public static final String MODID = "pillaging_pillagers";
}