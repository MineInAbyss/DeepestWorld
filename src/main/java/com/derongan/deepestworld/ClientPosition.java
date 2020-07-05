package com.derongan.deepestworld;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ClientPosition implements Position {
    public static ClientPosition create(double newX, double newY, double newZ) {
        return new AutoValue_ClientPosition(newX, newY, newZ);
    }
}
