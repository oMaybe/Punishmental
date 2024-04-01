package net.ruxor.punishments.commands;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHelper {

    public static List<String> player(String input) {
        return Bukkit.getOnlinePlayers().stream()
                .map(p -> p.getName())
                .filter(name -> name.toLowerCase().startsWith(input.toLowerCase())) // Filter names that start with the input
                .collect(Collectors.toList());
    }
}
