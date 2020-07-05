package com.derongan.deepestworld;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DeeperPosition implements Position {
    public static DeeperPosition create(double newX, double newY, double newZ) {
        return new AutoValue_DeeperPosition(newX, newY, newZ);
    }
}
