package com.artemka091102.test_mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.artemka091102.test_mod.capabilities.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = Main.MODID, bus=EventBusSubscriber.Bus.FORGE)

public class EventsHandler {
	static final Logger LOGGER = LogManager.getLogger();

	@SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(INbtCap.class, new Storage(), CompoundHandler::new);
    }
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		PlayerEntity player = event.player;
		CompoundNBT nbt = getPlayerData(player);

		int ticksUntilRaid = nbt.getInt("ticksUntilRaid");
		if (ticksUntilRaid-- == 0) {
			ticksUntilRaid = player.getRNG().nextInt(600) + 600;
		}
		if (ticksUntilRaid == 1)
		    LOGGER.debug("Рейд начался!");
		nbt.putInt("ticksUntilRaid", ticksUntilRaid);
	}
	@SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(Main.MODID, "playernbt"), new PlayerNbtProvider());
        }
    }
	
	static CompoundNBT getPlayerData(PlayerEntity player) {
        return player.getCapability(PlayerNbtProvider.CAPABILITY_PLAYERNBT).map(INbtCap::getCompound).orElseGet(CompoundNBT::new);
    }
}