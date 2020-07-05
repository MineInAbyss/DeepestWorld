package com.derongan.deepestworld.command;

import com.derongan.deepestworld.CoordinateConversions;
import com.derongan.deepestworld.DeeperPosition;
import com.derongan.deepestworld.ServerPosition;
import com.derongan.deepestworld.WindowManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommands implements CommandExecutor {
    private CoordinateConversions coordinateConversions;
    private WindowManager windowManager;

    public DebugCommands(CoordinateConversions coordinateConversions, WindowManager windowManager) {
        this.coordinateConversions = coordinateConversions;
        this.windowManager = windowManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            switch (command.getLabel()) {
                case "deeper":
                    DeeperPosition coords = coordinateConversions.convertToDeeper(ServerPosition.create(((Player) sender).getLocation()));
                    sender.sendMessage(coords.toString());
                    return true;
                case "window":
                    sender.sendMessage(windowManager.get((Player) sender).toString());
                    return true;
                case "actual":
                    sender.sendMessage(((Player) sender).getLocation().toVector().toString());
                    return true;
            }
        }
        return false;
    }
}
