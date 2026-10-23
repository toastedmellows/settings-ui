package com.example.settings;

import org.bukkit.Material;

import java.util.List;

/**
 * Every toggle shown in the /settings menu. To add a new setting, add a constant here
 * and read it with {@link SettingsManager#isEnabled}. The menu lays itself out automatically
 * (up to 9 settings).
 */
public enum Setting {
    PUBLIC_CHAT("public_chat", Material.PAPER, "Public Chat", true,
            List.of("Show chat messages sent by other players.")),
    JOIN_QUIT("join_quit", Material.OAK_DOOR, "Join / Leave Messages", true,
            List.of("Show messages when players join or leave.")),
    TPA_REQUESTS("tpa_requests", Material.ENDER_PEARL, "TPA Requests", true,
            List.of("Let other players send you teleport requests", "(/tpa and /tpahere).")),
    ADVANCEMENTS("advancements", Material.EXPERIENCE_BOTTLE, "Advancement Messages", true,
            List.of("Show messages when players earn advancements.")),
    MENTION_SOUND("mention_sound", Material.NOTE_BLOCK, "Mention Sound", true,
            List.of("Play a sound when someone says your name in chat."));

    private final String key;
    private final Material icon;
    private final String displayName;
    private final boolean defaultValue;
    private final List<String> description;

    Setting(String key, Material icon, String displayName, boolean defaultValue, List<String> description) {
        this.key = key;
        this.icon = icon;
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public String key() { return key; }
    public Material icon() { return icon; }
    public String displayName() { return displayName; }
    public boolean defaultValue() { return defaultValue; }
    public List<String> description() { return description; }
}
