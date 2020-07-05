package com.derongan.deepestworld;

public final class CoordinateConversions {

    private final SectionLayout sectionLayout;

    public CoordinateConversions(SectionLayout sectionLayout) {
        this.sectionLayout = sectionLayout;
    }

    public DeeperPosition convertToDeeper(ClientPosition clientPosition, ClientWindow clientWindow) {
        return DeeperPosition.create(clientPosition.getX(), clientPosition.getY() + clientWindow.getYBottom(), clientPosition.getZ());
    }

    public DeeperPosition convertToDeeper(ServerPosition serverPosition) {
        SectionLayout.Section section = sectionLayout.getSection(serverPosition);

        return DeeperPosition.create(serverPosition.getX() - section.getCoordinates().getFirst().chunkX() * 16, serverPosition.getY() + section.getIndex() * 256, serverPosition.getZ() - section.getCoordinates().getFirst().chunkZ() * 16);
    }

    public ServerPosition convertToServer(DeeperPosition deeperPosition) {
        SectionLayout.Section section = sectionLayout.getSection(deeperPosition);
        ChunkCoord chunkCoord = section.getCoordinates().getFirst();
        return ServerPosition.create(deeperPosition.getX() + chunkCoord.chunkX() * 16,
                deeperPosition.getY() % 256,
                deeperPosition.getZ() + chunkCoord.chunkZ() * 16);
    }

    public ClientPosition convertToClient(DeeperPosition deeperPosition, ClientWindow clientWindow) {
        return ClientPosition.create(deeperPosition.getX(), deeperPosition.getY() - clientWindow.getYBottom(), deeperPosition.getZ());
    }
}
