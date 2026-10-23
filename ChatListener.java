package com.example.settings;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public final class ChatListener implements Listener {

    private final SettingsPlugin plugin;
    private final SettingsManager manager;

    public ChatListener(SettingsPlugin plugin, SettingsManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();

        if (plugin.getConfig().getBoolean("block-sending-when-disabled", true)
                && !manager.isEnabled(sender, Setting.PUBLIC_CHAT)) {
            event.setCancelled(true);
            sender.sendMessage(Component.text(
                    "You have public chat turned off. Use /settings to turn it back on.", NamedTextColor.RED));
            return;
        }

        // Hide the message from players who turned public chat off.
        event.viewers().removeIf(viewer ->
                viewer instanceof Player p && !manager.isEnabled(p, Setting.PUBLIC_CHAT));

        // Mention sound.
        String text = PlainTextComponentSerializer.plainText().serialize(event.message()).toLowerCase();
        List<Player> mentioned = new ArrayList<>();
        for (Audience audience : event.viewers()) {
            if (audience instanceof Player p
                    && !p.equals(sender)
                    && manager.isEnabled(p, Setting.MENTION_SOUND)
                    && text.contains(p.getName().toLowerCase())) {
                mentioned.add(p);
            }
        }
        if (!mentioned.isEmpty()) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (Player p : mentioned) {
                    if (p.isOnline()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f);
                    }
                }
            });
        }
    }
}
