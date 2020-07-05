package com.derongan.deepestworld;

import org.bukkit.entity.Player;

/**
 * Calculate a {@link ClientWindow} based on a players current position.
 */
public interface ClientWindowInitiaizer {
    ClientWindow calculateWindow(Player player);
}
