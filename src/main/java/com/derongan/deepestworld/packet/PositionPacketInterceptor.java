package com.derongan.deepestworld.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.derongan.deepestworld.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutPosition;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class PositionPacketInterceptor extends PacketAdapter {
    private static final Set<PacketType> PLAY_IN = ImmutableSet.of(PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK);
    private static final Set<PacketType> PLAY_OUT = ImmutableSet.of(PacketType.Play.Server.POSITION);

    private DeepestWorld plugin;
    private final CoordinateConversions coordinateConversions;
    private final WindowManager windowManager;

    public PositionPacketInterceptor(DeepestWorld plugin, CoordinateConversions coordinateConversions, WindowManager windowManager) {
        super(plugin, Iterables.concat(PLAY_IN, PLAY_OUT));
        this.plugin = plugin;
        this.coordinateConversions = coordinateConversions;
        this.windowManager = windowManager;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        List<Double> position = event.getPacket().getDoubles().getValues();
        Preconditions.checkState(position.size() >= 3);
        ClientPosition clientPosition = ClientPosition.create(position.get(0), position.get(1), position.get(2));

        DeeperPosition deeperPosition = coordinateConversions.convertToDeeper(clientPosition, windowManager.get(event.getPlayer()));
        ServerPosition serverPosition = coordinateConversions.convertToServer(deeperPosition);

        event.getPacket().getDoubles().write(0, serverPosition.getX());
        event.getPacket().getDoubles().write(1, serverPosition.getY());
        event.getPacket().getDoubles().write(2, serverPosition.getZ());

        moveIllegally(event.getPlayer(), serverPosition);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        StructureModifier<Set<PacketPlayOutPosition.EnumPlayerTeleportFlags>> enumModifier = event
                .getPacket()
                .getSets(new EnumWrappers.EnumConverter<>(PlayerTeleportFlags.class, PacketPlayOutPosition.EnumPlayerTeleportFlags.class));
        enumModifier.write(0, ImmutableSet.of());

        ServerPosition serverPosition = ServerPosition.create(event.getPlayer().getLocation());


        if (windowManager.recalculate(event.getPlayer())) {
//            PacketContainer clientView = new PacketContainer(PacketType.Play.Server.VIEW_CENTRE);
//            clientView.getIntegers().write(0, (int) (clientPosition.getX() / 16));
//            clientView.getIntegers().write(1, (int) (clientPosition.getZ() / 16));
//            try {
//                plugin.protocolManager.sendServerPacket(event.getPlayer(), clientView);
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
        }

        DeeperPosition deeperPosition = coordinateConversions.convertToDeeper(serverPosition);
        ClientPosition clientPosition = coordinateConversions.convertToClient(deeperPosition, windowManager.get(event.getPlayer()));

        event.getPacket().getDoubles().write(0, clientPosition.getX());
        event.getPacket().getDoubles().write(1, clientPosition.getY());
        event.getPacket().getDoubles().write(2, clientPosition.getZ());
    }

    private static void moveIllegally(Player player, ServerPosition newPosition) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        //Set location and update last location
        entityPlayer.f(newPosition.getX(), newPosition.getY(), newPosition.getZ());
    }
}
