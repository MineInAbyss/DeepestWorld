package com.derongan.deepestworld;

import org.bukkit.util.Vector;

public interface Position {
    double getX();

    double getY();

    double getZ();

    default Vector toVector() {
        return new Vector(getX(), getY(), getZ());
    }
}
