# AGENTS.md - Anti-Vacuum Issue Analysis

## Problem Statement

### The Core Issue
Server-side optimization mods like [MoreCulling](https://github.com/FxMorin/MoreCulling) aggressively cull (hide) blocks and chunks to improve server performance. This creates "vacuum" areas where blocks appear missing or as air, even though they should contain actual block data.

### Symptoms
- Blocks appear as air/void when they should be solid blocks
- Chunks appear partially loaded or completely missing
- Visual artifacts where terrain looks "hollow" or incomplete
- Missing block textures in areas that should have blocks
- Blocks that "pop in" when approached from certain angles

### Server Context
- Servers running MoreCulling or similar optimization mods
- High-performance servers that prioritize FPS over visual completeness
- Servers that use aggressive chunk culling to reduce network traffic

## What Works: Freecam Method

### The Successful Approach
Using a freecam mod to navigate to vacuum areas and **clicking on the missing blocks** successfully loads the real block data.

### Why Freecam Clicking Works
1. **Direct Block Interaction**: Clicking on a block position sends a direct interaction request to the server
2. **Forced Server Response**: The server must respond with actual block data when a player attempts to interact
3. **Bypasses Culling Logic**: Direct interaction requests may bypass or override optimization culling decisions
4. **Immediate Network Request**: Creates an immediate, specific network request for that exact block position
5. **Player Action Context**: Server treats it as a legitimate player action rather than automated data requests

### Technical Details of Freecam Success
- **Action**: Right-click or left-click on vacuum block positions
- **Network Protocol**: Sends player interaction packets to server
- **Server Response**: Server must provide actual block data for interaction validation
- **Result**: Missing blocks become visible and properly loaded

## Failed Approaches

### Approach 1: Basic Block State Access (F8 Normal Mode)
**Method**: Systematically access `world.getBlockState(pos)` for every block in a chunk

**Why It Failed**:
- Server optimization mods may intercept or ignore these requests
- Block state requests might be cached or filtered by culling logic
- No direct player interaction context to force server response
- Optimization mods designed to handle and ignore bulk data requests

**Code Example**:
```java
BlockState blockState = world.getBlockState(pos);
world.getBlockEntity(pos);
```

### Approach 2: Aggressive Multi-Pass Processing (F9 Aggressive Mode)
**Method**: Multiple passes with varied patterns, surrounding chunks, unpredictable access

**Why It Failed**:
- Still using the same underlying `getBlockState()` API that can be optimized away
- Optimization mods may detect and ignore bulk access patterns regardless of complexity
- No actual player interaction to trigger mandatory server response
- More sophisticated but still fundamentally the same approach as basic access

**Code Example**:
```java
// Multiple passes with different strategies
for (int pass = 0; pass < 3; pass++) {
    // Various access patterns but still using getBlockState()
    world.getBlockState(pos);
}
```

### Approach 3: Chunk Section Forcing
**Method**: Directly access chunk sections and force chunk loading

**Why It Failed**:
- Chunk sections may be loaded but still contain optimized/culled block data
- Server-side culling happens at the block data level, not chunk loading level
- Chunk being "loaded" doesn't guarantee accurate block data if optimization mods are filtering it

### Approach 4: Block Entity Access
**Method**: Access block entities to trigger additional server requests

**Why It Failed**:
- Block entities may not exist for culled blocks
- Server may return null/empty data for optimized areas
- Doesn't create the same interaction context as player clicking

## Root Cause Analysis

### Why Our Approaches Don't Work
1. **API Level Issue**: We're using client-side world access APIs that can be intercepted by optimization mods
2. **No Player Context**: Our requests don't have the same priority/context as actual player interactions
3. **Bulk Request Detection**: Optimization mods may detect and filter automated bulk requests
4. **Wrong Network Layer**: We're not sending the same type of network packets that freecam clicking sends

### Why Freecam Clicking Works
1. **Player Interaction Packets**: Sends actual player interaction network packets
2. **Mandatory Server Response**: Server must respond to player interactions for game mechanics
3. **Bypasses Optimization**: Player interactions may have higher priority than optimization culling
4. **Direct Network Protocol**: Uses the same network protocol as legitimate player actions

## Potential Solutions to Investigate

### Solution 1: Simulate Player Interactions
**Concept**: Instead of accessing block states, simulate actual player interactions (clicks) on vacuum block positions

**Technical Approach**:
- Send player interaction packets to server
- Simulate right-click or left-click events on block positions
- Use the same network protocol that freecam uses for clicking

**Challenges**:
- Need to identify the correct network packets for player interactions
- May require more complex implementation
- Could be detected as automated behavior

### Solution 2: Hybrid Approach
**Concept**: Combine block state access with simulated player interactions

**Technical Approach**:
- First, identify vacuum areas using current block state access
- Then, send targeted player interaction packets for those specific positions
- Focus on quality over quantity - fewer but more effective requests

### Solution 3: Network Packet Analysis
**Concept**: Analyze what network packets freecam sends when clicking on blocks

**Technical Approach**:
- Study freecam mod source code or network traffic
- Identify the exact packets sent during successful block clicking
- Replicate those packets in our mod

### Solution 4: Player Movement Simulation
**Concept**: Simulate player movement and looking at blocks to trigger natural loading

**Technical Approach**:
- Simulate player looking at vacuum block positions
- May trigger natural chunk/block loading mechanisms
- Less aggressive than direct interaction but might be more compatible

## Next Steps

1. **Research freecam mod implementation** - How exactly does it send click interactions?
2. **Network packet analysis** - What packets are sent when freecam successfully loads blocks?
3. **Player interaction API investigation** - How to properly simulate player interactions in Fabric?
4. **Test targeted approach** - Instead of bulk processing, try targeted interaction simulation

## Key Insight

The fundamental difference between our failed approaches and the successful freecam method is:
- **Our approach**: Bulk data access requests that can be optimized away
- **Freecam approach**: Individual player interaction requests that must be honored

We need to shift from "requesting data" to "simulating player interactions" to achieve the same success as freecam clicking. 