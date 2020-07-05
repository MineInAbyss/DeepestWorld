package com.derongan.deepestworld;

import com.derongan.deepestworld.config.WorldConfiguration;

public class LinearSectionLayout implements SectionLayout {
    private final WorldConfiguration worldConfiguration;

    public LinearSectionLayout(WorldConfiguration worldConfiguration) {
        this.worldConfiguration = worldConfiguration;
    }

    @Override
    public Section getSection(DeeperPosition deeperPosition) {
        int index = (int) (deeperPosition.getY() / 256);
        return Section.builder()
                .setIndex(index)
                .setBottomLeft(ChunkCoord.create(index * worldConfiguration.sectionSizeInChunks(), 0))
                .setTopRight(ChunkCoord.create((index + 1) * worldConfiguration.sectionSizeInChunks() - 1, worldConfiguration.sectionSizeInChunks() - 1))
                .build();
    }

    @Override
    public Section getSection(ServerPosition serverPosition) {
        int chunkX = (int) (serverPosition.getX() / 16);
        int index = chunkX / worldConfiguration.sectionSizeInChunks();
        return Section.builder()
                .setIndex(index)
                .setBottomLeft(ChunkCoord.create(index * worldConfiguration.sectionSizeInChunks(), 0))
                .setTopRight(ChunkCoord.create((index + 1) * worldConfiguration.sectionSizeInChunks() - 1, worldConfiguration.sectionSizeInChunks() - 1))
                .build();
    }
}
