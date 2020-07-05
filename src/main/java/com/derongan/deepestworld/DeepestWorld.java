package com.derongan.deepestworld;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.derongan.deepestworld.command.DebugCommands;
import com.derongan.deepestworld.packet.AdvancementInterceptor;
import com.derongan.deepestworld.packet.ChunkPacketInterceptor;
import com.derongan.deepestworld.packet.PositionPacketInterceptor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DeepestWorld extends JavaPlugin implements Listener {
    SectionLayout sectionLayout = new LinearSectionLayout(() -> 32);

    CoordinateConversions coordinateConversions = new CoordinateConversions(sectionLayout);
    private WindowManager windowManager;
    public ProtocolManager protocolManager;

    @Override
    public void onLoad() {
        //instrument
    }

    @Override
    public void onEnable() {
        TickCounter task = new TickCounter();
        windowManager = new WindowManager(new ChunkFixedWindowInitializer(coordinateConversions, task));
        getServer().getOnlinePlayers().forEach(windowManager::recalculate);
        PositionPacketInterceptor positionPacketInterceptor = new PositionPacketInterceptor(this, coordinateConversions, windowManager);
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(positionPacketInterceptor);
        protocolManager.addPacketListener(new ChunkPacketInterceptor(this, coordinateConversions, windowManager, sectionLayout));
        protocolManager.addPacketListener(new AdvancementInterceptor(this));

        getServer().getPluginManager().registerEvents(this, this);

        DebugCommands executor = new DebugCommands(coordinateConversions, windowManager);
        getCommand("deeper").setExecutor(executor);
        getCommand("window").setExecutor(executor);
        getCommand("actual").setExecutor(executor);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, task, 0, 1);
    }

    @Override
    public void onDisable() {
        protocolManager.removePacketListeners(this);
    }

    /**
     * Calculate a players initial chunk window on join.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        windowManager.recalculate(playerJoinEvent.getPlayer());
    }
}
