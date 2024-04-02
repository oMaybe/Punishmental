package net.ruxor.punishments.util;

import net.ruxor.punishments.Punishmental;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.List;

public class Messages {

    public static final String NO_PERMISSION;
    public static final String NOT_A_PLAYER;
    public static final String INVALID_PROFILE;

    public static final List<String> BAN_MESSAGE;
    public static final String PUBLIC_BAN_MESSAGE;
    public static final String SILENT_BAN_MESSAGE;

    public static final String PUBLIC_UNBAN_MESSAGE;
    public static final String SILENT_UNBAN_MESSAGE;

    public static final List<String> BLACKLIST_MESSAGE;
    public static final String PUBLIC_BLACKLIST_MESSAGE;
    public static final String SILENT_BLACKLIST_MESSAGE;

    public static final String PUBLIC_UNBLACKLIST_MESSAGE;
    public static final String SILENT_UNBLACKLIST_MESSAGE;

    public static final List<String> ALT_BLACKLIST_MESSAGE;
    public static final String SILENT_ALT_EVASION;
    public static final String SILENT_POSSIBLE_ALT_EVASION;

    public static final String MUTE_MESSAGE;
    public static final String PUBLIC_MUTE_MESSAGE;
    public static final String SILENT_MUTE_MESSAGE;

    public static final String UNMUTE_MESSAGE;
    public static final String PUBLIC_UNMUTE_MESSAGE;
    public static final String SILENT_UNMUTE_MESSAGE;

    public static final String WARN_MESSAGE;
    public static final String SILENT_WARN_MESSAGE;
    public static final String UNWARN_MESSAGE;
    public static final String SILENT_UNWARN_MESSAGE;

    public static final String LOAD_MESSAGE;

    static {
        NO_PERMISSION = CC.translate((String) getOrDefault("no_perm"));
        NOT_A_PLAYER = CC.translate((String) getOrDefault("not_a_player"));
        INVALID_PROFILE = CC.translate((String) getOrDefault("invalid_profile"));

        BAN_MESSAGE = CC.translate((List<String>) getOrDefault("ban_message"));
        PUBLIC_BAN_MESSAGE = CC.translate((String) getOrDefault("public_ban_message"));
        SILENT_BAN_MESSAGE = CC.translate((String) getOrDefault("silent_ban_message"));

        PUBLIC_UNBAN_MESSAGE = CC.translate((String) getOrDefault("public_unban_message"));
        SILENT_UNBAN_MESSAGE = CC.translate((String) getOrDefault("silent_unban_message"));

        BLACKLIST_MESSAGE = CC.translate((List<String>) getOrDefault("blacklist_message"));
        PUBLIC_BLACKLIST_MESSAGE = CC.translate((String) getOrDefault("public_blacklist_message"));
        SILENT_BLACKLIST_MESSAGE = CC.translate((String) getOrDefault("silent_blacklist_message"));

        PUBLIC_UNBLACKLIST_MESSAGE = CC.translate((String) getOrDefault("public_unblacklist_message"));
        SILENT_UNBLACKLIST_MESSAGE = CC.translate((String) getOrDefault("silent_unblacklist_message"));

        ALT_BLACKLIST_MESSAGE = CC.translate((List<String>) getOrDefault("alt_blacklist_message"));
        SILENT_ALT_EVASION = CC.translate((String) getOrDefault("silent_alt_evasion"));
        SILENT_POSSIBLE_ALT_EVASION = CC.translate((String) getOrDefault("silent_possible_alt_evasion"));

        MUTE_MESSAGE = CC.translate((String) getOrDefault("mute_message"));
        PUBLIC_MUTE_MESSAGE = CC.translate((String) getOrDefault("public_mute_message"));
        SILENT_MUTE_MESSAGE = CC.translate((String) getOrDefault("silent_mute_message"));

        UNMUTE_MESSAGE = CC.translate((String) getOrDefault("unmute_message"));
        PUBLIC_UNMUTE_MESSAGE = CC.translate((String) getOrDefault("public_unmute_message"));
        SILENT_UNMUTE_MESSAGE = CC.translate((String) getOrDefault("silent_unmute_message"));

        WARN_MESSAGE = CC.translate((String) getOrDefault("warn_message"));
        SILENT_WARN_MESSAGE = CC.translate((String) getOrDefault("silent_warn_message"));
        UNWARN_MESSAGE = CC.translate((String) getOrDefault("unwarn_message"));
        SILENT_UNWARN_MESSAGE = CC.translate((String) getOrDefault("silent_unwarn_message"));

        LOAD_MESSAGE = CC.translate((String) getOrDefault("load_message"));
    }

    private static Object getOrDefault(String location){
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Punishmental.getInstance().getDataFolder(), "messages.yml"));
        return FileUtils.getOrDefault(config, location);
    }
}
