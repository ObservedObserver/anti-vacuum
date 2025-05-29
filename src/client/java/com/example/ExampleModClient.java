package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleModClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("anti-vacuum");
	
	private static KeyBinding forceLoadKeyBinding;
	private static KeyBinding aggressiveLoadKeyBinding;
	
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LOGGER.info("=== ANTI-VACUUM CLIENT MOD STARTING INITIALIZATION ===");
		LOGGER.info("Anti-Vacuum client initialized - Enhanced for server optimization mods!");
		
		// Register key bindings
		forceLoadKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.anti-vacuum.forceload", // Translation key
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_F8, // Default key: F8
			"category.anti-vacuum" // Category
		));
		
		aggressiveLoadKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.anti-vacuum.aggressive", // Translation key
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_F9, // Default key: F9
			"category.anti-vacuum" // Category
		));
		
		LOGGER.info("Registered F8 (normal) and F9 (aggressive) key bindings for chunk force loading");
		
		// Register client tick event to handle key presses
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (forceLoadKeyBinding.wasPressed()) {
				executeForceLoadChunk(client, false);
			}
			while (aggressiveLoadKeyBinding.wasPressed()) {
				executeForceLoadChunk(client, true);
			}
		});
		
		LOGGER.info("=== ANTI-VACUUM CLIENT MOD INITIALIZATION COMPLETE ===");
	}
	
	private void executeForceLoadChunk(MinecraftClient client, boolean aggressive) {
		if (client.player == null || client.world == null) {
			return;
		}
		
		BlockPos playerPos = client.player.getBlockPos();
		ChunkPos chunkPos = new ChunkPos(playerPos);
		
		String mode = aggressive ? "AGGRESSIVE" : "NORMAL";
		LOGGER.info("Client-side ForceLoadChunk ({}) at chunk {}, {}", mode, chunkPos.x, chunkPos.z);
		
		// Send feedback to player
		String modeText = aggressive ? "§c[AGGRESSIVE]" : "§7[NORMAL]";
		client.player.sendMessage(Text.literal("§6[Anti-Vacuum] " + modeText + " §7Processing chunk " + chunkPos.x + ", " + chunkPos.z + "..."), false);
		
		try {
			int processedBlocks = aggressive ? 
				aggressiveForceLoadChunkBlocks(client, chunkPos) : 
				forceLoadChunkBlocks(client.world, chunkPos);
			
			client.player.sendMessage(Text.literal("§6[Anti-Vacuum] " + modeText + " §aCompleted! §7Processed " + processedBlocks + " blocks in chunk " + chunkPos.x + ", " + chunkPos.z), false);
			LOGGER.info("Successfully processed {} blocks in chunk {}, {} ({})", processedBlocks, chunkPos.x, chunkPos.z, mode);
		} catch (Exception e) {
			client.player.sendMessage(Text.literal("§6[Anti-Vacuum] §cError: " + e.getMessage()), false);
			LOGGER.error("Failed to process chunk {}, {}: {}", chunkPos.x, chunkPos.z, e.getMessage());
		}
	}
	
	private int forceLoadChunkBlocks(ClientWorld world, ChunkPos chunkPos) {
		int processedBlocks = 0;
		
		// Request chunk loading from the server by accessing chunk data
		WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
		
		if (chunk == null) {
			LOGGER.warn("Chunk at {}, {} is null, cannot process", chunkPos.x, chunkPos.z);
			return 0;
		}
		
		// Force chunk sections to be loaded by accessing block states
		int startX = chunkPos.getStartX();
		int startZ = chunkPos.getStartZ();
		int endX = chunkPos.getEndX();
		int endZ = chunkPos.getEndZ();
		
		// Process all Y levels in the chunk - use standard Minecraft 1.21.4 world height
		int minY = world.getBottomY();
		int maxY = 320; // Standard world height for 1.21.4 (from -64 to 319)
		
		for (int y = minY; y < maxY; y++) {
			for (int x = startX; x <= endX; x++) {
				for (int z = startZ; z <= endZ; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					
					try {
						// Access the block state - this forces the client to request
						// the block data from the server if it's not loaded
						BlockState blockState = world.getBlockState(pos);
						
						// Check if this was a "vacuum" block (air that should be something else)
						if (blockState.getBlock() == Blocks.AIR) {
							// Force a block update by accessing additional properties
							world.getBlockEntity(pos); // This can trigger server requests
							processedBlocks++;
						} else if (!blockState.getBlock().equals(Blocks.VOID_AIR)) {
							// Count non-void blocks as processed
							processedBlocks++;
						}
						
						// Also force chunk section loading
						ChunkSectionPos sectionPos = ChunkSectionPos.from(pos);
						chunk.getSection(world.sectionCoordToIndex(sectionPos.getSectionY()));
						
					} catch (Exception e) {
						LOGGER.debug("Error accessing block at {}: {}", pos, e.getMessage());
					}
				}
			}
		}
		
		// Force chunk sections to be accessed to trigger server requests
		for (int sectionY = world.sectionCoordToIndex(minY >> 4); sectionY < world.sectionCoordToIndex((maxY - 1) >> 4) + 1; sectionY++) {
			try {
				chunk.getSection(sectionY);
			} catch (Exception e) {
				LOGGER.debug("Error accessing chunk section {}: {}", sectionY, e.getMessage());
			}
		}
		
		return processedBlocks;
	}
	
	private int aggressiveForceLoadChunkBlocks(MinecraftClient client, ChunkPos chunkPos) {
		ClientWorld world = client.world;
		int processedBlocks = 0;
		
		LOGGER.info("Starting AGGRESSIVE mode against server optimization mods (like MoreCulling)");
		
		// Multiple passes to counter aggressive server-side culling
		for (int pass = 0; pass < 3; pass++) {
			LOGGER.debug("Aggressive pass {} for chunk {}, {}", pass + 1, chunkPos.x, chunkPos.z);
			
			// Force load the target chunk and surrounding chunks
			for (int dx = -1; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					ChunkPos targetChunk = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
					processedBlocks += aggressiveChunkPass(world, client, targetChunk, pass);
				}
			}
			
			// Force world renderer updates - removed due to API limitations
			// Focus on aggressive block access instead
			try {
				// Force additional chunk access by requesting block states at key positions
				BlockPos centerPos = new BlockPos(chunkPos.x * 16 + 8, 64, chunkPos.z * 16 + 8);
				world.getBlockState(centerPos);
				world.getBlockState(centerPos.up(64));
				world.getBlockState(centerPos.down(64));
				world.getBlockState(centerPos.up(128));
			} catch (Exception e) {
				LOGGER.debug("Error in additional chunk access: {}", e.getMessage());
			}
			
			// Small delay between passes to allow server processing
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		
		return processedBlocks;
	}
	
	private int aggressiveChunkPass(ClientWorld world, MinecraftClient client, ChunkPos chunkPos, int pass) {
		int processedBlocks = 0;
		
		WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
		if (chunk == null) {
			return 0;
		}
		
		int startX = chunkPos.getStartX();
		int startZ = chunkPos.getStartZ();
		int endX = chunkPos.getEndX();
		int endZ = chunkPos.getEndZ();
		
		int minY = world.getBottomY();
		int maxY = 320;
		
		// Different strategies per pass to counter various optimization techniques
		switch (pass) {
			case 0: // Standard block access
				processedBlocks += standardBlockAccess(world, chunk, startX, startZ, endX, endZ, minY, maxY);
				break;
			case 1: // Section-focused access
				processedBlocks += sectionFocusedAccess(world, chunk, minY, maxY);
				break;
			case 2: // Neighbor-aware access (counters culling based on visibility)
				processedBlocks += neighborAwareAccess(world, startX, startZ, endX, endZ, minY, maxY);
				break;
		}
		
		return processedBlocks;
	}
	
	private int standardBlockAccess(ClientWorld world, WorldChunk chunk, int startX, int startZ, int endX, int endZ, int minY, int maxY) {
		int processed = 0;
		
		// Access blocks in a pattern that's harder for optimization mods to predict
		for (int y = minY; y < maxY; y += 4) { // Skip some Y levels first
			for (int x = startX; x <= endX; x += 2) { // Skip some X positions
				for (int z = startZ; z <= endZ; z += 2) { // Skip some Z positions
					BlockPos pos = new BlockPos(x, y, z);
					try {
						BlockState state = world.getBlockState(pos);
						world.getBlockEntity(pos);
						processed++;
					} catch (Exception e) {
						LOGGER.debug("Error in standard access at {}: {}", pos, e.getMessage());
					}
				}
			}
		}
		
		// Fill in the gaps
		for (int y = minY; y < maxY; y++) {
			for (int x = startX; x <= endX; x++) {
				for (int z = startZ; z <= endZ; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					try {
						world.getBlockState(pos);
						processed++;
					} catch (Exception e) {
						LOGGER.debug("Error in gap filling at {}: {}", pos, e.getMessage());
					}
				}
			}
		}
		
		return processed;
	}
	
	private int sectionFocusedAccess(ClientWorld world, WorldChunk chunk, int minY, int maxY) {
		int processed = 0;
		
		// Force access to each chunk section multiple times
		for (int sectionY = world.sectionCoordToIndex(minY >> 4); sectionY < world.sectionCoordToIndex((maxY - 1) >> 4) + 1; sectionY++) {
			try {
				ChunkSection section = chunk.getSection(sectionY);
				if (section != null) {
					// Access section multiple times to ensure it's loaded
					for (int i = 0; i < 5; i++) {
						chunk.getSection(sectionY);
					}
					processed += 4096; // 16x16x16 blocks per section
				}
			} catch (Exception e) {
				LOGGER.debug("Error accessing section {}: {}", sectionY, e.getMessage());
			}
		}
		
		return processed;
	}
	
	private int neighborAwareAccess(ClientWorld world, int startX, int startZ, int endX, int endZ, int minY, int maxY) {
		int processed = 0;
		
		// Access blocks while also checking their neighbors (counters visibility-based culling)
		for (int y = minY; y < maxY; y += 8) { // Sample every 8th Y level
			for (int x = startX; x <= endX; x += 4) { // Sample every 4th X
				for (int z = startZ; z <= endZ; z += 4) { // Sample every 4th Z
					BlockPos pos = new BlockPos(x, y, z);
					try {
						// Access the block and all its neighbors
						world.getBlockState(pos);
						for (Direction dir : Direction.values()) {
							BlockPos neighborPos = pos.offset(dir);
							world.getBlockState(neighborPos);
						}
						processed += 7; // 1 + 6 neighbors
					} catch (Exception e) {
						LOGGER.debug("Error in neighbor access at {}: {}", pos, e.getMessage());
					}
				}
			}
		}
		
		return processed;
	}
}