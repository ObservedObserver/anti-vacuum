package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.block.BlockState;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	public static final String MOD_ID = "anti-vacuum";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Anti-Vacuum mod initialized!");
		
		// Register the forceloadchunk command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("forceloadchunk")
				.requires(source -> source.hasPermissionLevel(2)) // Require OP level 2
				.executes(this::executeForceLoadChunk));
		});
	}
	
	private int executeForceLoadChunk(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		ServerWorld world = source.getWorld();
		BlockPos playerPos = BlockPos.ofFloored(source.getPosition());
		ChunkPos chunkPos = new ChunkPos(playerPos);
		
		source.sendFeedback(() -> Text.literal("§6[Anti-Vacuum] §7Starting chunk force load at chunk " + chunkPos.x + ", " + chunkPos.z + "..."), false);
		
		try {
			// Force load the chunk if it's not already loaded
			WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
			
			int blocksProcessed = 0;
			int minY = world.getBottomY();
			int maxY = world.getHeight() + minY - 1; // Use getHeight() to get the world height
			
			// Process all blocks in the chunk from bottom to top
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = minY; y <= maxY; y++) {
						BlockPos blockPos = new BlockPos(chunkPos.getStartX() + x, y, chunkPos.getStartZ() + z);
						
						// Force block state retrieval - this eliminates vacuum-like artifacts
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
			source.sendFeedback(() -> Text.literal("§6[Anti-Vacuum] §aCompleted! §7Processed " + finalBlocksProcessed + " blocks in chunk " + chunkPos.x + ", " + chunkPos.z), false);
			
			LOGGER.info("Force loaded chunk at {}, {} with {} blocks processed", chunkPos.x, chunkPos.z, blocksProcessed);
			
		} catch (Exception e) {
			source.sendFeedback(() -> Text.literal("§6[Anti-Vacuum] §cError: Failed to force load chunk - " + e.getMessage()), false);
			LOGGER.error("Failed to force load chunk at {}, {}: {}", chunkPos.x, chunkPos.z, e.getMessage());
			return 0;
		}
		
		return 1;
	}
}