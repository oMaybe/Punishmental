package net.ruxor.punishments.util;

import net.ruxor.punishments.Punishmental;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    // replaces only keys and values (doesnt include things like comments)
    public static void loadDefaults(File file, String fileName){
        Bukkit.getLogger().info("Loading defaults for " + fileName );
        Bukkit.getLogger().info("File: " + file.getAbsolutePath());

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        YamlConfiguration internalConfig = YamlConfiguration.loadConfiguration(Punishmental.getInstance().getStringResource(fileName));

        List<String> missingKeys = internalConfig.getKeys(false).stream()
                .filter(key -> !config.contains(key))
                .toList();

        if(!missingKeys.isEmpty()) {
            Bukkit.getLogger().warning(String.format("Regenerating %d missing keys...", missingKeys.size()));
            missingKeys.forEach(missingKey -> config.set(missingKey, internalConfig.get(missingKey)));
            try {
                config.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().severe("Failed to save the config. e=" + e.getMessage());
            }
        }
    }

    // Replace the entire file with the default file
    public static void replaceDefaults(File file, String fileName){
        Bukkit.getLogger().info("Replacing entire file for " + fileName);
        Bukkit.getLogger().info("File: " + file.getAbsolutePath());

        BufferedReader internalConfig = new BufferedReader(Punishmental.getInstance().getStringResource(fileName));

        ArrayList<String> lines = new ArrayList<>();
        try {
            String line;
            while ((line = internalConfig.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to read the config. e=" + e.getMessage());
        }

        write(file, lines);
    }

    public static void write(File file, List<String> lines) {
        try {
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static Object getOrDefault(FileConfiguration file, String location){
        Object o = file.get(location);
        if (o == null) {
            Bukkit.getLogger().warning(file.getName() + " - Couldn't find the location for " + location + ". Attempting to use default values.");
            Bukkit.getLogger().warning("Alternate method: " + file.getName() + " with location: " + location);

            loadDefaults(new File(Punishmental.getInstance().getDataFolder(), file.getName()), file.getName());
            Object defValue = file.get(location);
            if (defValue == null){
                Bukkit.getLogger().severe(file.getName() + " - Couldn't find default value for " + location + ". Please contact dev immediately!");
                return null;
            }
            return defValue;
        }

        return o;
    }

    public static Object getOrDefaultConfig(String location){
        return getOrDefault(Punishmental.getInstance().getConfig(), location);
    }

    public enum FileAction {
        RELOAD,
        DEFAULTS,
        REPLACE
    }
}
