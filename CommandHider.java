package com.example.settings;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

/** Hides the "settings:settings" duplicate from players' tab-complete. */
public final class CommandHider implements Listener {

    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event) {
        event.getCommands().remove("settings:settings");
    }
}
