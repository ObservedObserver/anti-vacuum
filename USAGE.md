# Anti-Vacuum Mod Usage Guide

## Quick Start

1. **Install the mod** in your `mods` folder alongside Fabric API
2. **Start your Minecraft world** 
3. **Ensure you have OP permissions** (level 2 or higher)
4. **Use the command** `/forceloadchunk` while standing in the chunk you want to process

## Command Details

### `/forceloadchunk`

**Purpose**: Forces loading of all blocks in the current chunk to eliminate pseudo-vacuum artifacts

**Requirements**: 
- OP level 2 or higher
- Must be executed by a player (not console)

**What it does**:
- Identifies the chunk you're currently standing in
- Processes every block from world bottom (Y: -64) to world top (Y: 319)
- Forces block state retrieval for each position
- Loads block entities if present
- Provides feedback on progress and completion

**Expected Output**:
```
[Anti-Vacuum] Starting chunk force load at chunk 5, -3...
[Anti-Vacuum] Completed! Processed 98304 blocks in chunk 5, -3
```

## When to Use

- **Visual Artifacts**: When you see "vacuum" or missing block textures
- **Chunk Loading Issues**: When chunks appear partially loaded
- **After World Generation**: To ensure all blocks are properly loaded
- **Before Important Builds**: To guarantee stable terrain

## Performance Notes

- Processing a full chunk takes a few seconds
- The mod processes approximately 98,304 blocks per chunk (16x16x384)
- Safe to use during gameplay - designed for stability
- No permanent changes to world data

## Troubleshooting

**Command not found**: Ensure you have OP permissions and the mod is properly installed

**Permission denied**: You need OP level 2 or higher to use this command

**Error messages**: Check the console/logs for detailed error information

## Technical Details

- **Minecraft Version**: 1.21.4
- **Chunk Size**: 16x16 blocks horizontally
- **Height Range**: World bottom to world top (typically -64 to 319)
- **Block Processing**: Forces retrieval of block states and block entities
- **Safety**: Includes error handling to prevent crashes 