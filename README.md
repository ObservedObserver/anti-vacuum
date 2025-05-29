# Anti-Vacuum Mod

A **client-side** Minecraft Fabric mod that forces loading of "pseudo-vacuum" blocks to eliminate visual artifacts and ensure proper chunk data loading. **Enhanced to work against server-side optimization mods like [MoreCulling](https://github.com/FxMorin/MoreCulling)** that aggressively cull chunks and blocks.

## Features

- **Client-Side Only**: No server installation required - works on any server
- **Dual Processing Modes**: 
  - **F8 (Normal)**: Standard chunk processing for typical vacuum issues
  - **F9 (Aggressive)**: Enhanced processing designed to counter server optimization mods
- **Anti-Optimization**: Specifically designed to work against MoreCulling and similar server-side culling mods
- **Vacuum Elimination**: Forces full block data retrieval to eliminate vacuum-like visual artifacts
- **Comprehensive Processing**: Processes all blocks from world bottom to top (-64 to 319)
- **Real-time Feedback**: Provides clear feedback on operation progress and completion
- **Universal Compatibility**: Works with any Minecraft server without requiring server-side mods
- **Safe Operation**: Only requests data from server, doesn't modify anything

## How It Works

The mod addresses "vacuum" optimization issues using two approaches:

### **Normal Mode (F8)**
1. **Systematically accessing block states** in the current chunk
2. **Triggering automatic server requests** for missing block data
3. **Forcing chunk section loading** to ensure complete data retrieval
4. **Mimicking freecam behavior** that successfully loads vacuum areas

### **Aggressive Mode (F9) - Anti-MoreCulling**
Specifically designed to counter server optimization mods:
1. **Multi-pass processing** with 3 different strategies
2. **Surrounding chunk loading** (processes 9 chunks total)
3. **Unpredictable access patterns** harder for optimization mods to predict
4. **Section-focused repeated access** to force server responses
5. **Neighbor-aware processing** to counter visibility-based culling
6. **Persistent requests** with delays to overcome server throttling

This approach works because when the client persistently requests missing block data using varied patterns, even aggressive server-side optimization mods must eventually respond with the actual block information.

## Usage

1. **Install the mod** in your Fabric mods folder (client-side only)
2. **Join any Minecraft server** (no server-side installation needed)
3. **Navigate** to chunks with vacuum artifacts or missing blocks
4. **Choose your processing mode**:
   - **Press F8** for normal processing (standard vacuum issues)
   - **Press F9** for aggressive processing (servers with MoreCulling/optimization mods)
5. **Wait for completion** - you'll see progress messages in chat

### Key Bindings
- **F8** - Normal Force Load (current chunk only)
- **F9** - Aggressive Force Load (current + surrounding chunks, anti-optimization)

### When to Use Each Mode

**Normal Mode (F8):**
- Standard vacuum artifacts
- Lightly optimized servers
- Quick chunk processing (few seconds)

**Aggressive Mode (F9):**
- Servers running MoreCulling or similar optimization mods
- Persistent vacuum areas that don't respond to normal processing
- Heavily optimized servers with aggressive culling
- When F8 doesn't work (10-30 seconds processing time)

## Installation

1. Download and install [Fabric Loader](https://fabricmc.net/use/)
2. Download [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
3. Place both the Fabric API and this mod's JAR file in your `mods` folder
4. Launch Minecraft with the Fabric profile

## Technical Details

- **Minecraft Version**: 1.21.4
- **Fabric Loader**: 0.16.9+
- **Java**: 21+
- **Dependencies**: Fabric API
- **Environment**: Client-side only

## Advantages

- ✅ **No Server Installation**: Works on any server
- ✅ **Universal Compatibility**: Compatible with all server types
- ✅ **No Permissions Required**: No OP or admin access needed
- ✅ **Safe Operation**: Only requests data, doesn't modify anything
- ✅ **Immediate Effect**: Processes chunks in real-time
- ✅ **Anti-Optimization**: Counters aggressive server-side culling mods
- ✅ **Dual Modes**: Choose appropriate processing level for your server

## Building

```bash
./gradlew build
```

The built JAR will be located in `build/libs/`.

## Documentation

- [Client-Side Approach Details](CLIENT_SIDE_APPROACH.md) - Technical explanation of how the mod works against optimization mods
- [Usage Guide](USAGE.md) - Detailed usage instructions for both modes

## License

This project is licensed under CC0-1.0 - see the LICENSE file for details.
