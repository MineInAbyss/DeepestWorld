package com.derongan.deepestworld;

import com.google.auto.value.AutoValue;
import org.bukkit.Location;

@AutoValue
public abstract class ServerPosition implements Position {
    public static ServerPosition create(double newX, double newY, double newZ) {
        return new AutoValue_ServerPosition(newX, newY, newZ);
    }

    public static ServerPosition create(Location location) {
        return new AutoValue_ServerPosition(location.getX(), location.getY(), location.getZ());
    }
}
