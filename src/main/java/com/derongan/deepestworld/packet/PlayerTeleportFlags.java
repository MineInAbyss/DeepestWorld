package com.derongan.deepestworld.packet;

public enum PlayerTeleportFlags {
    X(0),
    Y(1),
    Z(2),
    Y_ROT(3),
    X_ROT(4);

    private int val;

    PlayerTeleportFlags(int val) {
        this.val = val;
    }
}