# Deepest World

Deepest world is a plugin for bukkit servers that makes your server Deep. It removes the 256 block height limit without requiring client side modification or any server patches.

## Terminology

### Section
A section is a square group of contiguous chunks. Sections are conceptually "stacked" on top of each other in Deepest World to provide depth.

### Chunk Segment
A chunk segment is a 16x16 part of a chunk. Each chunk contains 16 segments. The segment at the bottom is segment 0 and so on up to segment 15.

### Client Window
The client window defines what `Y` value of chunk segments a client can see. In all cases the window is `256` blocks high. In the most simplistic case has its `y`<sub>`lower`</sub> set to multiples of `256`. When a player exceeds `256` or descends below `0` the window shifts `256` blocks.

For more seamless transitions we can remove this constraint. Instead a window can contain parts of multiple sections. When the player moves far enough away from the center of the window, the window is recalculated.

### Deeper Coordinates
These coordinates represent where a player would be if the sections where actually contiguous. The first section contains `Y` coordinates `[0,256)`, the second `[256,512)`, the third `[512,768)` and so on. The `X` and `Z` coordinates are bounded by the size of the section. This means that the length and width of sections determine the domain for `X` and `Z`. For example the location one above `(0,255,0)` is `(0,256,0)` in the deeper coordinate system and will be on the second section.

### Server Coordinates
This is the easiest to understand coordinate. It represents where the rest of the minecraft server will interpret something to be. For example something located on the second section at its origin would have server coordinate `(section_width, 0, section_length)`.

### Client Coordinates
These coordinates represent where the clients *think* they are. `X` and `Z` values will always be the same as Deeper `X` and `Z` values. The `Y` value depends on the current window. For example if the window is fixed to multiples of `256` client coordinates will always be equivilant to `(x`<sub>`deeper`</sub>`, y`<sub>`server`</sub>`, z`<sub>`deeper`</sub>`)`.

## Coordinate Transformation
Coordinate transformation is one of the most important parts of Deepest World. Thankfully, these transformations are fairly simple as well. All transformations are conceptually done along the following path.

`server <-> deeper <-> client`

The current window must be known to perform these conversions.

```
section = (section_x, section_z)
server = (server_x, server_y, server_z)
client = (client_x, client_y, client_z)
deeper = (deeper_x, deeper_y, deeper_z)
window = (win_bottom, win_top)
win_top > win_bottom

server = (deeper_x + section_x, deeper_y % 256, deeper_z + section_z)
client = (deeper_x, deeper_y - window_bottom, deeper_z)
```

`window` is always known, as is `section`. This makes these coordinate conversions relatively easy to solve.

## Packet Manipulation
Deepest world uses packet manipulation to bring together the multiple coordinate systems.

### Client Bound Packets
Client bound packets have all locations referenced translated from server coordinates to client coordinates. Client Bound Packets are sent infrequently- only when the player moves incorrectly, teleports, changes dimension, or collides with something.

### Server Bound Packets
Server bound packets have all locations referenced translated from client coordinates to server coordinates.

## Chunks
Chunk management is the most complex part of Deepest World. 

Basically, the PlayerChunkMap needs to be hijacked to keep chunks properly loaded/sent to the player. 

We replace PlayerChunkMap with a wrapper, WindowAwarePlayerChunkMap. This chunk map is aware of what chunks need to be kept loaded for a players window.

This makes actual chunk manipulation fairly easy. Instead of complex chunk rearangement, we simply shift chunk data so that sections line up. The server takes care of sending both chunks, we simply truncate and shift it.

The wrapper is very straight forward as well. We simply need to swap the `PlayerProvider` out for one that honors a players window.

To do this we simply extend the `ChunkMap` class and use reflection to replace the instance of it in `ServerChunkCache`. The extended version will provide an overloaded `getPlayers` method.

Spigot complicates this. Each world has its own ChunkProvider as well as its own WorldServer (secondary world server for non overworld). We need to replace the chunk provider for the worlds we are interested in, and eventually all worlds?

Default Impl:
```
public Stream<EntityPlayer> a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
    return this.playerMap.a(chunkcoordintpair.pair()).filter((entityplayer) -> {
        int i = b(chunkcoordintpair, entityplayer, true);
        return i > this.viewDistance ? false : !flag || i == this.viewDistance;
    });
}
```

More or less this returns true if the chunks are close enough to the player.

Interestingly the distance a player is to a chunk is calculated as the max of distance in x and z in a static method. If we can simply replace this static method on the class definition, happy happy joy joy.

```
private static int a(ChunkCoordIntPair chunkcoordintpair, int i, int j) {
    int k = chunkcoordintpair.x - i;
    int l = chunkcoordintpair.z - j;
    return Math.max(Math.abs(k), Math.abs(l));
}
```

We can use ByteBuddy to swap out this function at runtime. This removes complicated spigot reflection.

The next issue is hijacking the cache to resend chunks within the players view window if needed...

[Update View Position](https://wiki.vg/Protocol#Update_View_Position) packet may be important!

## Player Manipulation
Alongside packet manipulation, players need to be teleported client side when the client window changes. Additionally, when switching sections a server side teleportation needs to occur that does not result in sending a teleportation packet to the client.

Player position is actually more complicated than I initially thought.

Packets are unruly and multiple could be received per tick. 

When a packet is received from the client, it could be pre or post teleport depending on network lag.

For the edge case of a window being the size of a chunk, we need to always consider the client location to be on the current window, even if the client does not know it has been teleported yet (otherwise collisions will not be properly calculated).

Possible incoming packets in one tick:

```
254 -> 254
256 -> 0 new window
255 -> 255 (out of order)
257 -> 1 new window
```


Assumption: Out of order packets are discarded before modification.