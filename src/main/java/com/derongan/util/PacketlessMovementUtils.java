package com.derongan.util;

import com.google.common.base.Preconditions;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PacketlessMovementUtils {
    public static void teleportReflectively(Player player, Vector newPosition) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.lastX = 3.0;
        entityPlayer.setPositionRaw(newPosition.getX(), newPosition.getY(), newPosition.getZ());
    }
}
