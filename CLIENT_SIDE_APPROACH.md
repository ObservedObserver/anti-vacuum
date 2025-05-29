# Client-Side Anti-Vacuum Mod Approach

## Overview

This mod has been redesigned as a **client-side only** solution to address "vacuum" block loading issues in Minecraft, specifically designed to work against server-side optimization mods like [MoreCulling](https://github.com/FxMorin/MoreCulling). The mod works by mimicking the behavior of freecam mods when they successfully trigger chunk loading by clicking on vacuum areas.

## How It Works

### The Problem
- Server-side optimization mods like MoreCulling aggressively cull (hide) chunks and blocks to improve performance
- This creates visual artifacts where blocks appear missing or as air ("vacuum" areas)
- Standard chunk loading requests may not be sufficient against aggressive server-side culling
- Freecam mods can sometimes fix this by triggering server-side chunk loading requests when clicking on vacuum areas

### The Solution
The client-side mod forces chunk loading using **two modes**:

#### **Normal Mode (F8)**
1. **Accessing Block States**: Systematically requests block state data for every position in a chunk
2. **Triggering Server Requests**: When the client accesses missing block data, it automatically sends requests to the server
3. **Forcing Chunk Section Loading**: Explicitly accesses chunk sections to ensure they're loaded
4. **Block Entity Processing**: Requests block entity data which can trigger additional server responses

#### **Aggressive Mode (F9) - Anti-MoreCulling**
Specifically designed to counter server optimization mods:

1. **Multi-Pass Processing**: Performs 3 different passes with different strategies
2. **Surrounding Chunk Loading**: Processes the target chunk plus all 8 surrounding chunks
3. **Unpredictable Access Patterns**: Uses varied block access patterns that are harder for optimization mods to predict
4. **Section-Focused Access**: Repeatedly accesses chunk sections to force server responses
5. **Neighbor-Aware Processing**: Accesses blocks along with their neighbors to counter visibility-based culling
6. **Persistent Requests**: Multiple attempts with delays to overcome server-side throttling

### Key Features

- **F8 Key Binding**: Normal mode - process the chunk you're currently standing in
- **F9 Key Binding**: Aggressive mode - enhanced processing against server optimization mods
- **Comprehensive Processing**: Processes all blocks from world bottom (-64) to world top (319)
- **Smart Block Detection**: Focuses on air blocks that might be "vacuum" artifacts
- **Real-time Feedback**: Shows progress and completion messages to the player
- **Error Handling**: Gracefully handles errors and provides feedback

## Technical Implementation

### Normal Mode Processing
```java
// Access every block in the chunk to trigger server requests
BlockState blockState = world.getBlockState(pos);
world.getBlockEntity(pos); // Triggers additional server requests
chunk.getSection(sectionY); // Forces chunk section loading
```

### Aggressive Mode Processing
```java
// Multi-pass approach with different strategies
for (int pass = 0; pass < 3; pass++) {
    // Process target chunk + 8 surrounding chunks
    for (int dx = -1; dx <= 1; dx++) {
        for (int dz = -1; dz <= 1; dz++) {
            // Different strategy per pass:
            // Pass 0: Unpredictable block access patterns
            // Pass 1: Section-focused repeated access
            // Pass 2: Neighbor-aware access (counters visibility culling)
        }
    }
    Thread.sleep(50); // Allow server processing between passes
}
```

### Why This Works Against MoreCulling
1. **Mimics Freecam Behavior**: Uses the same principle as freecam clicking on vacuum areas
2. **Counters Predictable Patterns**: Uses varied access patterns that optimization mods can't easily predict
3. **Overwhelms Culling Logic**: Multiple passes and surrounding chunk access make it harder to cull
4. **Forces Server Response**: Server must send actual block data when persistently requested
5. **No Server-Side Code Needed**: Works with any server, no mod installation required

## Usage

1. **Install the mod** in your client's `mods` folder
2. **Join any server** (no server-side installation needed)
3. **Navigate to problematic chunks** with vacuum artifacts
4. **Press F8** for normal processing or **F9** for aggressive anti-optimization processing
5. **Wait for completion** - the mod will show progress messages

### When to Use Each Mode

**Normal Mode (F8):**
- Standard vacuum artifacts
- Lightly optimized servers
- Quick chunk processing

**Aggressive Mode (F9):**
- Servers running MoreCulling or similar optimization mods
- Persistent vacuum areas that don't respond to normal processing
- Heavily optimized servers with aggressive culling

## Advantages of Client-Side Approach

- ✅ **No Server Installation**: Works on any server
- ✅ **Universal Compatibility**: Compatible with all server types
- ✅ **Safe Operation**: Only requests data, doesn't modify anything
- ✅ **Immediate Effect**: Processes chunks in real-time
- ✅ **User Control**: Player decides when and where to process chunks
- ✅ **Anti-Optimization**: Specifically designed to counter server optimization mods

## Performance Considerations

### Normal Mode
- **Processing Time**: Takes a few seconds per chunk (16x16x384 blocks)
- **Network Usage**: Generates server requests for missing block data
- **Memory Impact**: Minimal - only processes one chunk at a time
- **Server Load**: Comparable to normal chunk loading operations

### Aggressive Mode
- **Processing Time**: Takes 10-30 seconds per chunk (processes 9 chunks with 3 passes each)
- **Network Usage**: High - generates many server requests across multiple chunks
- **Memory Impact**: Moderate - processes multiple chunks simultaneously
- **Server Load**: Higher - designed to overwhelm optimization culling

## Comparison with Server-Side Approach

| Aspect | Client-Side | Server-Side |
|--------|-------------|-------------|
| Installation | Client only | Server required |
| Compatibility | Universal | Server-specific |
| Permissions | None needed | Admin access |
| Effectiveness vs MoreCulling | High (Aggressive Mode) | Very High |
| Maintenance | Easy | Complex |

## Future Enhancements

- **Auto-Detection**: Automatically detect when aggressive mode is needed
- **Adaptive Patterns**: Learn and adapt to specific server optimization patterns
- **Performance Optimization**: Optimize block access patterns based on server response
- **Visual Indicators**: Show processed areas and optimization mod detection 