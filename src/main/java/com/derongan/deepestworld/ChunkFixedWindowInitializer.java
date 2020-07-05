package com.derongan.deepestworld;

import org.bukkit.entity.Player;

class ChunkFixedWindowInitializer implements ClientWindowInitiaizer {
    private final CoordinateConversions coordinateConversions;
    private TickCounter tickCounter;

    public ChunkFixedWindowInitializer(CoordinateConversions coordinateConversions, TickCounter tickCounter) {
        this.coordinateConversions = coordinateConversions;
        this.tickCounter = tickCounter;
    }

    @Override
    public ClientWindow calculateWindow(Player player) {
        DeeperPosition deeperPosition = coordinateConversions.convertToDeeper(ServerPosition.create(player.getLocation()));

        int bottomY = Math.max(0, (int) (deeperPosition.getY() / 256) * 256);
        return ClientWindow.create(bottomY);
    }

}
