package com.derongan.deepestworld;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ChunkCoord {
    public abstract int chunkX();

    public abstract int chunkZ();

    public static ChunkCoord create(int x, int z) {
        return new AutoValue_ChunkCoord(x, z);
    }
}
