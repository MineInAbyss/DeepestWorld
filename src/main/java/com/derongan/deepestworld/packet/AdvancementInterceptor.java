package com.derongan.deepestworld.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

public class AdvancementInterceptor extends PacketAdapter {
    public AdvancementInterceptor(Plugin plugin) {
        super(plugin, PacketType.Play.Client.getInstance().values());
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        System.out.println(event.getPacketType().name());
    }
}
