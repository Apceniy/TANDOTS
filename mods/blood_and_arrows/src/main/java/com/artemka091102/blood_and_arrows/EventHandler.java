package com.artemka091102.blood_and_arrows;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Main.MODID, bus=EventBusSubscriber.Bus.FORGE)

public class EventHandler {
	static final Logger LOGGER = LogManager.getLogger();
	@SubscribeEvent
	public static void DamageHit(LivingAttackEvent event) {
		Entity entity = event.getEntity();
		DamageSource source = event.getSource();
		World world = entity.world;
		if (source == DamageSource.GENERIC) {
			for (int i = 0; i < 100; i++) {
				entity.world.addParticle((IParticleData) ParticleTypes.FIREWORK, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ, (world.getRandom().nextFloat() - 0.5)/3, (world.getRandom().nextFloat() - 0.5)/3, (world.getRandom().nextFloat() - 0.5)/3);
			}
		}
	}
}