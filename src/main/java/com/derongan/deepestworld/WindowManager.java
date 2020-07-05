package com.derongan.deepestworld;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class WindowManager {
    private final HashMap<UUID, ClientWindow> playerToWindowMap;
    private final ClientWindowInitiaizer clientWindowInitiaizer;

    public WindowManager(ClientWindowInitiaizer clientWindowInitiaizer) {
        this.clientWindowInitiaizer = clientWindowInitiaizer;
        this.playerToWindowMap = new HashMap<>();
    }

    public ClientWindow get(Player player) {
        return playerToWindowMap.computeIfAbsent(player.getUniqueId(), uuid -> clientWindowInitiaizer.calculateWindow(player));
    }

    public boolean recalculate(Player player) {
        ClientWindow newWindow = clientWindowInitiaizer.calculateWindow(player);
        ClientWindow oldWindow = playerToWindowMap.get(player.getUniqueId());
        playerToWindowMap.put(player.getUniqueId(), newWindow);

        boolean recalcd = oldWindow != newWindow;
        if (recalcd) {
            System.out.println(String.format("Window recalculated to %s", newWindow.toString()));
        }
        return recalcd;
    }
}
