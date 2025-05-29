package com.example;

import net.fabricmc.api.ModInitializer;
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
		// This mod is primarily client-side, but we still need this entrypoint
		// for the mod to be recognized properly by Fabric
		LOGGER.info("=== ANTI-VACUUM MOD INITIALIZATION ===");
		LOGGER.info("Anti-Vacuum mod initialized!");
		LOGGER.info("Mod ID: {}", MOD_ID);
		LOGGER.info("This is a client-side mod - main functionality is in ExampleModClient");
		LOGGER.info("=== ANTI-VACUUM MOD INITIALIZATION COMPLETE ===");
	}
}