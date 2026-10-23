package com.example.settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The 4-row /settings inventory. Settings on row 2, ON/OFF indicators on row 3, close button below. */
public final class SettingsMenu implements InventoryHolder {

    private static final int SIZE = 36;
    public static final int CLOSE_SLOT = 31;

    private final SettingsManager manager;
    private final Player viewer;
    private final Inventory inventory;
    private final Map<Integer, Setting> slots = new HashMap<>();

    public SettingsMenu(SettingsManager manager, Player viewer) {
        this.manager = manager;
        this.viewer = viewer;
        this.inventory = Bukkit.createInventory(this, SIZE, Component.text("Settings", NamedTextColor.DARK_GRAY));

        Setting[] all = Setting.values();
        int start = 9 + (9 - all.length) / 2;
        for (int i = 0; i < all.length; i++) {
            int slot = start + i;
            slots.put(slot, all[i]);
            slots.put(slot + 9, all[i]); // the indicator below is clickable too
        }
        refresh();
    }

    public void open() {
        viewer.openInventory(inventory);
    }

    public Setting settingAt(int slot) {
        return slots.get(slot);
    }

    /** Rebuilds every item from the player's current settings. */
    public void refresh() {
        ItemStack filler = named(Material.GRAY_STAINED_GLASS_PANE, Component.empty());
        for (int i = 0; i < SIZE; i++) {
            inventory.setItem(i, filler);
        }

        for (Map.Entry<Integer, Setting> entry : slots.entrySet()) {
            int slot = entry.getKey();
            Setting setting = entry.getValue();
            boolean on = manager.isEnabled(viewer, setting);
            if (slot < 18) {
                inventory.setItem(slot, settingItem(setting, on));
            } else {
                inventory.setItem(slot, statusItem(setting, on));
            }
        }

        inventory.setItem(CLOSE_SLOT, named(Material.BARRIER, text("Close", NamedTextColor.RED)));
    }

    private ItemStack settingItem(Setting setting, boolean on) {
        ItemStack item = new ItemStack(setting.icon());
        item.editMeta(meta -> {
            meta.displayName(text(setting.displayName(), on ? NamedTextColor.GREEN : NamedTextColor.RED)
                    .decoration(TextDecoration.BOLD, true));
            List<Component> lore = new ArrayList<>();
            for (String line : setting.description()) {
                lore.add(text(line, NamedTextColor.GRAY));
            }
            lore.add(Component.empty());
            lore.add(on ? text("Currently: ON", NamedTextColor.GREEN) : text("Currently: OFF", NamedTextColor.RED));
            lore.add(text("Click to toggle", NamedTextColor.YELLOW));
            meta.lore(lore);
            meta.setEnchantmentGlintOverride(on);
        });
        return item;
    }

    private ItemStack statusItem(Setting setting, boolean on) {
        return named(on ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE,
                text(setting.displayName() + ": " + (on ? "ON" : "OFF"), on ? NamedTextColor.GREEN : NamedTextColor.RED));
    }

    private static ItemStack named(Material material, Component name) {
        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> meta.displayName(name));
        return item;
    }

    private static Component text(String text, NamedTextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
