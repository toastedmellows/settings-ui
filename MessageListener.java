package com.example.settings;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Join/leave and advancement messages are normally sent to everyone. We cancel the
 * default broadcast and send it ourselves only to players who left that setting on.
 */
public final class MessageListener implements Listener {

    private final SettingsManager manager;

    public MessageListener(SettingsManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Component message = event.joinMessage();
        if (message == null) return;
        event.joinMessage(null);
        manager.broadcast(Setting.JOIN_QUIT, message);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Component message = event.quitMessage();
        if (message == null) return;
        event.quitMessage(null);
        manager.broadcast(Setting.JOIN_QUIT, message);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        Component message = event.message();
        if (message == null) return;
        event.message(null);
        manager.broadcast(Setting.ADVANCEMENTS, message);
    }
}
