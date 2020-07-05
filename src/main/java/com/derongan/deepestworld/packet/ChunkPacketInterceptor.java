package com.derongan.deepestworld.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.derongan.deepestworld.*;
import net.minecraft.server.v1_15_R1.BiomeStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ChunkPacketInterceptor extends PacketAdapter {
    private static final PacketType mapChunk = PacketType.Play.Server.MAP_CHUNK;
    private static final PacketType unloadChunk = PacketType.Play.Server.UNLOAD_CHUNK;
    private static final PacketType viewCentre = PacketType.Play.Server.VIEW_CENTRE;
    private CoordinateConversions coordinateConversions;
    private WindowManager windowManager;
    private SectionLayout sectionLayout;

    public ChunkPacketInterceptor(Plugin plugin, CoordinateConversions coordinateConversions, WindowManager windowManager, SectionLayout sectionLayout) {
        super(plugin, mapChunk, unloadChunk, viewCentre);
        this.coordinateConversions = coordinateConversions;
        this.windowManager = windowManager;
        this.sectionLayout = sectionLayout;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        int x = event.getPacket().getIntegers().getValues().get(0);
        int z = event.getPacket().getIntegers().getValues().get(1);

        ClientWindow clientWindow = windowManager.get(event.getPlayer());
        ServerPosition serverPosition = ServerPosition.create(x * 16, clientWindow.getYBottom(), z * 16);
        if (sectionLayout.getSection(serverPosition).equals(sectionLayout.getSection(ServerPosition.create(event.getPlayer().getLocation())))) {
            DeeperPosition deeperPosition = coordinateConversions.convertToDeeper(serverPosition);
            ClientPosition clientPosition = coordinateConversions.convertToClient(deeperPosition, clientWindow);

            int newX = (int) (clientPosition.getX() / 16);
            int newZ = (int) (clientPosition.getZ() / 16);


            event.getPacket().getIntegers().write(0, newX);
            event.getPacket().getIntegers().write(1, newZ);

            if (event.getPacketType().equals(mapChunk)) {
                if (Bukkit.getWorld("world").isChunkLoaded(newX, newZ)) {
                    event.getPacket().getBooleans().write(0, false);
                    event.getPacket().getSpecificModifier(BiomeStorage.class).write(0, null);
                }
            } else {
                event.setCancelled(true);
            }

            if (newX == 0 && newZ == 0) {
                System.out.println(String.format("%s (%d,%d)->(%d,%d)", event.getPacketType().toString(), x, z, newX, newZ));
            }
        }
    }
}
