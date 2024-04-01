package net.ruxor.punishments.util;

import net.ruxor.punishments.Punishmental;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

public class Messages {

    public static String NO_PERMISSION = CC.translate((String) getOrDefault("no_perm"));
    public static String NOT_A_PLAYER = CC.translate((String) getOrDefault("not_a_player"));
    public static String INVALID_PROFILE = CC.translate((String) getOrDefault("invalid_profile"));

    public static ArrayList<String> BAN_MESSAGE = CC.translate((ArrayList<String>) getOrDefault("ban_message"));
    public static String PUBLIC_BAN_MESSAGE = CC.translate((String) getOrDefault("public_ban_message"));
    public static final String SILENT_BAN_MESSAGE = CC.translate((String) getOrDefault("silent_ban_message"));

    public static String PUBLIC_UNBAN_MESSAGE = CC.translate((String) getOrDefault("public_unban_message"));
    public static String SILENT_UNBAN_MESSAGE = CC.translate((String) getOrDefault("silent_unban_message"));

    public static final ArrayList<String> BLACKLIST_MESSAGE = CC.translate((ArrayList<String>) getOrDefault("blacklist_message"));
    public static final String PUBLIC_BLACKLIST_MESSAGE = CC.translate((String) getOrDefault("public_blacklist_message"));
    public static final String SILENT_BLACKLIST_MESSAGE = CC.translate((String) getOrDefault("silent_blacklist_message"));

    public static final String PUBLIC_UNBLACKLIST_MESSAGE = CC.translate((String) getOrDefault("public_unblacklist_message"));
    public static final String SILENT_UNBLACKLIST_MESSAGE = CC.translate((String) getOrDefault("silent_unblacklist_message"));

    public static final ArrayList<String> ALT_BLACKLIST_MESSAGE = CC.translate((ArrayList<String>) getOrDefault("alt_blacklist_message"));
    public static final String SILENT_ALT_EVASION = CC.translate((String) getOrDefault("alt_blacklist_message"));
    public static final String SILENT_POSSIBLE_ALT_EVASION = CC.translate((String) getOrDefault("silent_possible_alt_evasion"));

    public static final String MUTE_MESSAGE = CC.translate((String) getOrDefault("mute_message"));
    public static final String PUBLIC_MUTE_MESSAGE = CC.translate((String) getOrDefault("public_mute_message"));
    public static final String SILENT_MUTE_MESSAGE = CC.translate((String) getOrDefault("silent_mute_message"));

    public static final String UNMUTE_MESSAGE = CC.translate((String) getOrDefault("unmute_message"));
    public static final String PUBLIC_UNMUTE_MESSAGE = CC.translate((String) getOrDefault("public_unmute_message"));
    public static final String SILENT_UNMUTE_MESSAGE = CC.translate((String) getOrDefault("silent_unmute_message"));

    public static final String WARN_MESSAGE = CC.translate((String) getOrDefault("warn_message"));
    public static final String SILENT_WARN_MESSAGE = CC.translate((String) getOrDefault("silent_warn_message"));

    public static final String UNWARN_MESSAGE = CC.translate((String) getOrDefault("unwarn_message"));
    public static final String SILENT_UNWARN_MESSAGE = CC.translate((String) getOrDefault("silent_unwarn_message"));

    public static final String LOAD_MESSAGE = CC.translate((String) getOrDefault("load_message"));

    private static Object getOrDefault(String location){
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Punishmental.getInstance().getDataFolder(), "messages.yml"));
        return FileUtils.getOrDefault(config, location);
    }
}
