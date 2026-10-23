package com.example.settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Set;

/**
 * Blocks /tpa and /tpahere (EssentialsX or similar) when the target player has TPA Requests
 * turned off. Works by checking the command, so no EssentialsX dependency is needed.
 */
public final class TpaListener implements Listener {

    private static final Set<String> TPA_COMMANDS = Set.of(
            "tpa", "tpask", "call", "tpahere",
            "etpa", "etpask", "ecall", "etpahere");

    private final SettingsManager manager;

    public TpaListener(SettingsManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String[] parts = event.getMessage().substring(1).trim().split("\\s+");
        if (parts.length < 2) return;

        String command = parts[0].toLowerCase();
        int colon = command.indexOf(':'); // handles /essentials:tpa
        if (colon >= 0) command = command.substring(colon + 1);
        if (!TPA_COMMANDS.contains(command)) return;

        Player sender = event.getPlayer();
        Player target = Bukkit.getPlayer(parts[1]);
        if (target == null || target.equals(sender)) return;
        if (sender.hasPermission("settings.tpa.bypass")) return;

        if (!manager.isEnabled(target, Setting.TPA_REQUESTS)) {
            event.setCancelled(true);
            sender.sendMessage(Component.text(target.getName() + " is not accepting teleport requests.",
                    NamedTextColor.RED));
        }
    }
}
