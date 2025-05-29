# Anti-Vacuum Mod Usage Guide

## Quick Start

1. **Install the mod** in your client's `mods` folder alongside Fabric API
2. **Join any Minecraft server** (no server-side installation required)
3. **Navigate to problematic chunks** with vacuum artifacts
4. **Choose your processing mode**:
   - **Press F8** for normal processing (standard vacuum issues)
   - **Press F9** for aggressive processing (servers with MoreCulling/optimization mods)
5. **Wait for completion** - you'll see progress messages in chat

## Key Binding Details

### **F8 Key** - Normal Force Load

**Purpose**: Standard chunk processing for typical vacuum artifacts

**Requirements**: 
- Client-side mod installation only
- No special permissions needed
- Works on any server

**What it does**:
- Identifies the chunk you're currently standing in
- Processes every block from world bottom (Y: -64) to world top (Y: 319)
- Forces block state retrieval for each position
- Triggers server requests for missing block data
- Loads block entities if present
- Provides real-time feedback on progress and completion

**Expected Output**:
```
[Anti-Vacuum] [NORMAL] Processing chunk 5, -3...
[Anti-Vacuum] [NORMAL] Completed! Processed 98304 blocks in chunk 5, -3
```

**Processing Time**: 2-5 seconds per chunk

### **F9 Key** - Aggressive Force Load (Anti-MoreCulling)

**Purpose**: Enhanced processing specifically designed to counter server-side optimization mods like MoreCulling

**Requirements**: 
- Client-side mod installation only
- No special permissions needed
- Works on any server (especially those with optimization mods)

**What it does**:
- Identifies the chunk you're currently standing in **plus 8 surrounding chunks**
- Performs **3 different processing passes** with varied strategies:
  - **Pass 1**: Unpredictable block access patterns
  - **Pass 2**: Section-focused repeated access
  - **Pass 3**: Neighbor-aware processing (counters visibility-based culling)
- Uses delays between passes to allow server processing
- Forces additional chunk access at key positions
- Overwhelms server-side culling logic with persistent requests

**Expected Output**:
```
[Anti-Vacuum] [AGGRESSIVE] Processing chunk 5, -3...
[Anti-Vacuum] [AGGRESSIVE] Completed! Processed 294912 blocks in chunk 5, -3
```

**Processing Time**: 10-30 seconds per chunk (processes 9 chunks total)

## When to Use Each Mode

### **Normal Mode (F8) - Use When:**
- ✅ Standard vacuum artifacts on most servers
- ✅ Lightly optimized servers
- ✅ Quick processing is needed
- ✅ First attempt at fixing vacuum areas
- ✅ Server doesn't run aggressive optimization mods

### **Aggressive Mode (F9) - Use When:**
- ✅ Server runs MoreCulling or similar optimization mods
- ✅ Normal mode (F8) doesn't fix the vacuum areas
- ✅ Persistent vacuum areas that keep reappearing
- ✅ Heavily optimized servers with aggressive culling
- ✅ Large-scale vacuum artifacts
- ✅ You need maximum effectiveness regardless of processing time

## Server Optimization Mod Detection

**Signs your server uses aggressive optimization mods:**
- Vacuum areas reappear quickly after using F8
- Large areas of missing blocks or terrain
- Blocks appear and disappear when moving around
- F8 processing shows success but vacuum areas persist
- Server has very good performance but visual artifacts

**In these cases, use F9 (Aggressive Mode)**

## How Each Mode Works

### Normal Mode Technical Details
- **Chunk Coverage**: Current chunk only (16x16 blocks)
- **Block Processing**: Every block from Y -64 to Y 319
- **Strategy**: Sequential block state access
- **Network Requests**: Standard chunk loading requests
- **Server Impact**: Minimal - comparable to normal chunk loading

### Aggressive Mode Technical Details
- **Chunk Coverage**: 3x3 chunk area (48x48 blocks total)
- **Block Processing**: Multiple passes with different patterns
- **Strategy**: Multi-pass with unpredictable patterns
- **Network Requests**: High volume, varied timing
- **Server Impact**: Higher - designed to overwhelm culling logic

## Performance Notes

### Normal Mode (F8)
- **Processing Time**: 2-5 seconds
- **Network Usage**: Moderate
- **Memory Impact**: Minimal
- **Server Load**: Low
- **Success Rate**: High on standard servers

### Aggressive Mode (F9)
- **Processing Time**: 10-30 seconds
- **Network Usage**: High
- **Memory Impact**: Moderate
- **Server Load**: Higher (but safe)
- **Success Rate**: Very high on optimized servers

## Customization

### Changing the Key Bindings

1. Open Minecraft **Options** → **Controls**
2. Find the **Anti-Vacuum** category
3. You'll see two bindings:
   - **Force Load Chunk** (default: F8)
   - **Aggressive Force Load** (default: F9)
4. Click on either binding and press your desired key
5. Click **Done** to save

## Troubleshooting

**F8 doesn't work on vacuum areas**: 
- Try F9 (Aggressive Mode) - your server likely uses optimization mods
- Check if you're standing in the correct chunk
- Verify the mod is properly installed

**F9 takes too long**: 
- This is normal - aggressive mode processes 9 chunks with 3 passes each
- Don't press the key again while processing is active
- Wait for the completion message

**Neither key works**: 
- Check if the mod is properly installed in your `mods` folder
- Verify Fabric API is also installed
- Check the key bindings in Minecraft controls
- Look for error messages in the game log

**Vacuum areas reappear after processing**: 
- This indicates aggressive server-side optimization
- Use F9 (Aggressive Mode) instead of F8
- Some servers may require multiple F9 attempts
- Consider that some optimization is intentional server behavior

**Performance issues during processing**:
- F9 mode is intentionally resource-intensive
- Avoid processing multiple chunks simultaneously
- Wait for current processing to complete
- Consider server performance - some servers may be slower to respond

## Best Practices

### For Normal Servers
1. **Start with F8** - try normal mode first
2. **Process systematically** - work through problem areas methodically
3. **Wait for completion** - don't spam the keys
4. **Verify results** - check that vacuum areas are properly loaded

### For Optimized Servers (MoreCulling, etc.)
1. **Use F9 primarily** - aggressive mode is designed for these servers
2. **Be patient** - processing takes longer but is more effective
3. **Process key areas** - focus on important locations first
4. **Expect higher resource usage** - this is necessary to counter optimization

### General Tips
1. **Report persistent issues** - some vacuum problems may require server-side fixes
2. **Use sparingly** - only process chunks that actually have vacuum artifacts
3. **Monitor server response** - some servers may have rate limiting
4. **Combine with movement** - sometimes moving around helps trigger additional loading

## Comparison with Other Solutions

| Solution | Installation | Effectiveness vs MoreCulling | Speed | Permissions |
|----------|-------------|------------------------------|-------|-------------|
| F8 (Normal) | Client only | Moderate | Fast (2-5s) | None |
| F9 (Aggressive) | Client only | High | Slow (10-30s) | None |
| Server Commands | Server required | Very High | Instant | OP/Admin |
| Freecam Clicking | Client only | Moderate | Manual | None | 