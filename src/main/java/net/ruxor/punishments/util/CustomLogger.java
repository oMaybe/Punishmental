package net.ruxor.punishments.util;

import org.bukkit.Bukkit;

public class CustomLogger {

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(message));
    }
}
