# Anti-Vacuum Mod

A Minecraft Fabric mod that forces loading of "pseudo-vacuum" blocks to eliminate visual artifacts and ensure proper chunk data loading.

## Features

- **Force Chunk Loading**: Use the `/forceloadchunk` command to process all blocks in your current chunk
- **Vacuum Elimination**: Forces full block data retrieval to eliminate vacuum-like visual artifacts
- **Comprehensive Processing**: Processes all blocks from world bottom to top (Y-axis)
- **User Feedback**: Provides clear feedback on operation progress and completion
- **Stability**: Maintains game stability during chunk processing operations

## Usage

1. **Install the mod** in your Fabric mods folder
2. **Start your Minecraft world** (requires OP level 2 permissions)
3. **Navigate** to the chunk you want to process
4. **Run the command**: `/forceloadchunk`
5. **Wait for completion** - the mod will process all blocks in the current chunk

## Command

- `/forceloadchunk` - Forces loading of all blocks in the current chunk
  - Requires OP level 2 permissions
  - Processes blocks from world bottom to world top
  - Provides feedback on blocks processed and completion status

## Technical Details

- **Minecraft Version**: 1.21.4
- **Fabric Loader**: 0.16.9+
- **Java**: 21+
- **Dependencies**: Fabric API

## Installation

1. Download and install [Fabric Loader](https://fabricmc.net/use/)
2. Download [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
3. Place both the Fabric API and this mod's JAR file in your `mods` folder
4. Launch Minecraft with the Fabric profile

## Building

```bash
./gradlew build
```

The built JAR will be located in `build/libs/`.

## License

This project is licensed under CC0-1.0 - see the LICENSE file for details.
