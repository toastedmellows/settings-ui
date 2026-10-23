package com.example.settings;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/** Stores each player's settings in plugins/Settings/playerdata.yml. Thread-safe for reads. */
public final class SettingsManager {

    private final SettingsPlugin plugin;
    private final File file;
    private final Map<UUID, Map<Setting, Boolean>> data = new ConcurrentHashMap<>();
    private volatile boolean dirty;

    public SettingsManager(SettingsPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "playerdata.yml");
    }

    public void load() {
        data.clear();
        if (!file.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection players = yaml.getConfigurationSection("players");
        if (players == null) return;
        for (String id : players.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                continue;
            }
            ConfigurationSection section = players.getConfigurationSection(id);
            if (section == null) continue;
            for (Setting setting : Setting.values()) {
                if (section.isBoolean(setting.key())) {
                    values(uuid).put(setting, section.getBoolean(setting.key()));
                }
            }
        }
    }

    public synchronized void save() {
        if (!dirty) return;
        dirty = false;
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, Map<Setting, Boolean>> entry : data.entrySet()) {
            for (Map.Entry<Setting, Boolean> value : entry.getValue().entrySet()) {
                yaml.set("players." + entry.getKey() + "." + value.getKey().key(), value.getValue());
            }
        }
        try {
            plugin.getDataFolder().mkdirs();
            yaml.save(file);
        } catch (IOException e) {
            dirty = true;
            plugin.getLogger().log(Level.SEVERE, "Could not save playerdata.yml", e);
        }
    }

    private Map<Setting, Boolean> values(UUID uuid) {
        return data.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
    }

    public boolean isEnabled(UUID uuid, Setting setting) {
        Map<Setting, Boolean> map = data.get(uuid);
        if (map == null) return setting.defaultValue();
        return map.getOrDefault(setting, setting.defaultValue());
    }

    public boolean isEnabled(Player player, Setting setting) {
        return isEnabled(player.getUniqueId(), setting);
    }

    /** Flips the setting and returns the new value. */
    public boolean toggle(Player player, Setting setting) {
        boolean newValue = !isEnabled(player, setting);
        values(player.getUniqueId()).put(setting, newValue);
        dirty = true;
        return newValue;
    }

    /** Sends a message to everyone (and the console) that has the given setting enabled. */
    public void broadcast(Setting setting, Component message) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (isEnabled(online, setting)) {
                online.sendMessage(message);
            }
        }
        Bukkit.getConsoleSender().sendMessage(message);
    }
}
