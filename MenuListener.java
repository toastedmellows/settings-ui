package com.example.settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class MenuListener implements Listener {

    private final SettingsManager manager;

    public MenuListener(SettingsManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder(false) instanceof SettingsMenu menu)) return;
        event.setCancelled(true); // nothing can be taken, placed, or shift-clicked
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        int slot = event.getRawSlot();
        if (slot == SettingsMenu.CLOSE_SLOT) {
            player.closeInventory();
            return;
        }
        Setting setting = menu.settingAt(slot);
        if (setting == null) return;

        boolean now = manager.toggle(player, setting);
        menu.refresh();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, now ? 1.2f : 0.8f);
        if (setting == Setting.PUBLIC_CHAT && !now) {
            player.sendMessage(Component.text("Public chat is now hidden. Use /settings to turn it back on.",
                    NamedTextColor.YELLOW));
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder(false) instanceof SettingsMenu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        manager.save();
    }
}
