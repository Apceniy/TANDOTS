package com.artemka091102.pillaging_pillagers;

import java.util.Iterator;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.artemka091102.pillaging_pillagers.capabilities.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.EntityEvent.EnteringChunk;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = PillagingPillagers.MODID, bus=EventBusSubscriber.Bus.FORGE)

public class EventsHandler {
	static final Logger LOGGER = LogManager.getLogger();

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(INbtCap.class, new Storage(), CompoundHandler::new);
	}
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
			PlayerEntity player = event.player;
		CompoundNBT nbt = getCustomNBT(player);

		int ticksUntilRaid = nbt.getInt("ticksUntilRaid");
		if (ticksUntilRaid-- == 0) {
			ticksUntilRaid = player.getRNG().nextInt(1200) + 1200;
		}
		if (ticksUntilRaid == 1 && player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			ServerWorld serverworld = serverPlayer.getServerWorld();
			BlockPos playerPos = player.getPosition();
			BlockPos outpostPos = serverworld.findNearestStructure("PILLAGER_OUTPOST", playerPos, 10000, false);
			if (outpostPos != null) {
				int count = 1;
				/*
				float difficulty = (float)serverworld.getChunk((playerPos.getX()/16), (playerPos.getZ()/16)).getInhabitedTime()/serverworld.getGameTime();
				LOGGER.debug(playerChunk.getInhabitedTime());
				LOGGER.debug(serverworld.getGameTime());
				LOGGER.debug(difficulty);
				LOGGER.debug("");
				if (difficulty > 2.0F) {
				}
				*/
				Random random = serverworld.rand;
				int dx = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
				int dz = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
				BlockPos.MutableBlockPos mutablepos = new BlockPos.MutableBlockPos(outpostPos.add(7+dx, 0, 7+dz));
				for (int i = 0; i < count; i++) {
					serverworld.forceChunk(mutablepos.getX()/16, mutablepos.getZ()/16, true);
					mutablepos.setY(serverworld.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutablepos).getY());
					if (i == 0) {
						if (!func_222695_a(serverworld, mutablepos, random, player)) {
							break;
						}
					} else {
						func_222695_a(serverworld, mutablepos, random, player);
					}

					mutablepos.func_223471_o(mutablepos.getX() + random.nextInt(5) - random.nextInt(5));
					mutablepos.func_223472_q(mutablepos.getZ() + random.nextInt(5) - random.nextInt(5));
				}
			}
		}
		nbt.putInt("ticksUntilRaid", ticksUntilRaid);
	}
	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof PlayerEntity || event.getObject() instanceof PillagerEntity) {
			event.addCapability(new ResourceLocation(PillagingPillagers.MODID, "modnbt"), new NbtProvider());
		}
	}
	
	private static boolean func_222695_a(World worldIn, BlockPos p_222695_2_, Random random, PlayerEntity player) {
		if (!PatrollerEntity.func_223330_b(EntityType.PILLAGER, worldIn, SpawnReason.PATROL, p_222695_2_, random)) {
			return false;
		} else {
			PatrollerEntity patrollerentity = EntityType.PILLAGER.create(worldIn);
			if (patrollerentity != null) {
				patrollerentity.setPosition((double)p_222695_2_.getX(), (double)p_222695_2_.getY(), (double)p_222695_2_.getZ());
				patrollerentity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(p_222695_2_), SpawnReason.EVENT, (ILivingEntityData)null, (CompoundNBT)null);
				patrollerentity.enablePersistence();
				CompoundNBT nbt = getCustomNBT(patrollerentity);
				nbt.putUniqueId("playerTarget", player.getUniqueID());
				worldIn.addEntity(patrollerentity);
				return true;
			} else {
				return false;
			}
		}
	}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if(isEntityTowardsPlayer(entity)) {
			MobEntity mobEntity = (MobEntity) entity;
			mobEntity.goalSelector.addGoal(3, new MoveToPlayerGoal(mobEntity, /*0.7D*/1.5D));
		}
	}
	
	@SubscribeEvent
	public static void onEnteringChunk(EnteringChunk event) {
		Entity entity = event.getEntity();
		if (entity.world instanceof ServerWorld && isEntityTowardsPlayer(entity)) {
			ServerWorld world = (ServerWorld)entity.world;
			queueForceChunk(world, event.getNewChunkX(), event.getNewChunkZ());
			queueForceChunk(world, event.getOldChunkX(), event.getOldChunkZ());
		}
	}
	
	/*@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		World world = event.getWorld().getWorld();
		PillagingPillagers.LOADED_WORLDS.add(world);
		Iterator<PendingChunk<ServerWorld>> iterator = PendingChunk.LIST.iterator();
		while (iterator.hasNext()) {
			PendingChunk<ServerWorld> pending = iterator.next();
			if (pending.world == world) {
				if(testChunk(pending.world.getChunk(pending.chunkX, pending.chunkZ))) tryForceChunk(pending.world, pending.chunkX, pending.chunkZ, true);
				iterator.remove();
			}
		}
	}
	
	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event) {
		PillagingPillagers.LOADED_WORLDS.remove(event.getWorld().getWorld());
	}*/
	
	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		Iterator<PendingChunk<ServerWorld>> iterator = PendingChunk.LIST.iterator();
		while (iterator.hasNext()) {
			PendingChunk<ServerWorld> pending = iterator.next();
			if (pending.world == event.world) {
				if(testChunk(pending.world.getChunk(pending.chunkX, pending.chunkZ))) pending.world.forceChunk(pending.chunkX, pending.chunkZ, true);
				iterator.remove();
			}
		}
	}
	
	public static boolean queueForceChunk(ServerWorld world, int chunkX, int chunkZ) {
		//if (!world.chunkExists(chunkX, chunkZ)) return false;
		boolean flag = true;
		for (PendingChunk<ServerWorld> pending : PendingChunk.LIST) {
			if (pending.world == world && pending.chunkX == chunkX && pending.chunkZ == chunkZ)
				flag = false;
		}
		if (flag) PendingChunk.LIST.add(new PendingChunk<>(world, chunkX, chunkZ));
		return flag;
	}
	
	/*public static boolean tryForceChunk(ServerWorld world, int chunkX, int chunkZ, boolean add) {
		if (PillagingPillagers.LOADED_WORLDS.contains(world)) {
			world.forceChunk(chunkX, chunkZ, add);
			return true;
		}
		boolean flag = true;
		for (PendingChunk<ServerWorld> pending : PendingChunk.LIST) {
			if (pending.world == world && pending.chunkX == chunkX && pending.chunkZ == chunkZ)
				flag = false;
		}
		if (flag) PendingChunk.LIST.add(new PendingChunk<>(world, chunkX, chunkZ));
		return false;
	}*/
	
	public static boolean testChunk(Chunk chunk) {
		boolean flag = true;
		for (ClassInheritanceMultiMap<Entity> list : chunk.getEntityLists()) {
			while (list.iterator().hasNext()) {
				if (isEntityTowardsPlayer(list.iterator().next()))
					flag = false;
			}
		}
		return flag;
	}
	
	public static boolean isEntityTowardsPlayer(Entity entityIn) {
		CompoundNBT nbt = getCustomNBT(entityIn);
		return entityIn instanceof MobEntity && nbt.contains("playerTargetLeast") && nbt.contains("playerTargetMost");
	}
	
	public static CompoundNBT getCustomNBT(Entity entity) {
		return entity.getCapability(NbtProvider.CAPABILITY_NBT).map(INbtCap::getCompound).orElseGet(CompoundNBT::new);
	}
}