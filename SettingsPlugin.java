package com.example.settings;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class SettingsPlugin extends JavaPlugin {

    private SettingsManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        manager = new SettingsManager(this);
        manager.load();

        getServer().getPluginManager().registerEvents(new MenuListener(manager), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this, manager), this);
        getServer().getPluginManager().registerEvents(new MessageListener(manager), this);
        getServer().getPluginManager().registerEvents(new CommandHider(), this);
        getServer().getPluginManager().registerEvents(new TpaListener(manager), this);

        // Save changes every 5 minutes (also saved when a player leaves and on shutdown).
        getServer().getScheduler().runTaskTimerAsynchronously(this, manager::save, 6000L, 6000L);
    }

    @Override
    public void onDisable() {
        if (manager != null) {
            manager.save();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use /settings.");
            return true;
        }
        new SettingsMenu(manager, player).open();
        return true;
    }
}
