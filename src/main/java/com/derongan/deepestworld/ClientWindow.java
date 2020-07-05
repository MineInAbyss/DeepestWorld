package com.derongan.deepestworld;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;

@AutoValue
public abstract class ClientWindow {
    /**
     * Get the bottom of the clients window in blocks.
     */
    public abstract int getYBottom();

    public static ClientWindow create(int newYBottom) {
        Preconditions.checkArgument(newYBottom % 16 == 0, "ClientWindow must be ChunkSegment aligned");
        return new AutoValue_ClientWindow(newYBottom);
    }
}
