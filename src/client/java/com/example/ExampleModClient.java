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
import net.minecraft.block.BlockState;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleModClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("anti-vacuum");
	
	private static KeyBinding forceLoadKeyBinding;
	
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LOGGER.info("=== ANTI-VACUUM CLIENT MOD STARTING INITIALIZATION ===");
		LOGGER.info("Anti-Vacuum client initialized!");
		
		// Register key binding
		forceLoadKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.anti-vacuum.forceload", // Translation key
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_F8, // Default key: F8
			"category.anti-vacuum" // Category
		));
		
		LOGGER.info("Registered F8 key binding for chunk force loading");
		
		// Register client tick event to handle key presses
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (forceLoadKeyBinding.wasPressed()) {
				executeClientForceLoadChunk(client);
			}
		});
		
		LOGGER.info("=== ANTI-VACUUM CLIENT MOD INITIALIZATION COMPLETE ===");
	}
	
	private void executeClientForceLoadChunk(MinecraftClient client) {
		if (client.player == null || client.world == null) {
			return;
		}
		
		ClientWorld world = client.world;
		BlockPos playerPos = client.player.getBlockPos();
		ChunkPos chunkPos = new ChunkPos(playerPos);
		
		LOGGER.info("Client-side ForceLoadChunk executed at chunk {}, {}", chunkPos.x, chunkPos.z);
		
		// Send feedback to player
		client.player.sendMessage(Text.literal("§6[Anti-Vacuum] §7Starting client-side chunk force load at chunk " + chunkPos.x + ", " + chunkPos.z + "..."), false);
		
		try {
			int blocksProcessed = 0;
			int minY = world.getBottomY();
			int maxY = world.getHeight() + minY - 1;
			
			// Process all blocks in the chunk from bottom to top
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = minY; y <= maxY; y++) {
						BlockPos blockPos = new BlockPos(chunkPos.getStartX() + x, y, chunkPos.getStartZ() + z);
						
						// Force block state retrieval on client side
						BlockState blockState = world.getBlockState(blockPos);
						
						// Ensure the block data is fully loaded by accessing its properties
						if (blockState != null) {
							// Force block entity loading if present
							if (world.getBlockEntity(blockPos) != null) {
								world.getBlockEntity(blockPos);
							}
							blocksProcessed++;
						}
					}
				}
			}
			
			final int finalBlocksProcessed = blocksProcessed;
			client.player.sendMessage(Text.literal("§6[Anti-Vacuum] §aCompleted! §7Processed " + finalBlocksProcessed + " blocks in chunk " + chunkPos.x + ", " + chunkPos.z), false);
			
			LOGGER.info("Client-side force loaded chunk at {}, {} with {} blocks processed", chunkPos.x, chunkPos.z, blocksProcessed);
			
		} catch (Exception e) {
			client.player.sendMessage(Text.literal("§6[Anti-Vacuum] §cError: Failed to force load chunk - " + e.getMessage()), false);
			LOGGER.error("Failed to client-side force load chunk at {}, {}: {}", chunkPos.x, chunkPos.z, e.getMessage());
		}
	}
}