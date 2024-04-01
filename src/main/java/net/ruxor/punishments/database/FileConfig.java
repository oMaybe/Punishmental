package net.ruxor.punishments.database;

import lombok.Getter;
import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.util.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileConfig {

    @Getter
    private final String fileName;
    @Getter
    private final File file;
    private FileConfiguration config;

    public FileConfig(String fileName) {
        this.fileName = fileName;
        this.file = new File(Punishmental.getInstance().getDataFolder(), fileName);
        if (!file.exists()){
            this.file.getParentFile().mkdirs();
            try {
                boolean created = file.createNewFile();
                config = YamlConfiguration.loadConfiguration(file);
                if (created){
                    Punishmental.getInstance().getLogger().info("Config - Created a new " + fileName + " file!");
                    FileUtils.replaceDefaults(file, fileName);
                }else{
                    Punishmental.getInstance().getLogger().info("Config - " + fileName + " file appears to exist.");
                    FileUtils.loadDefaults(file, fileName);
                }
            } catch (IOException e) {
                Punishmental.getInstance().getLogger().severe("Config - Failed to create " + fileName + " file. e=" + e.getMessage());
                e.fillInStackTrace();
            }
        }else{
            FileUtils.loadDefaults(file, fileName);
        }

    }

    public void loadDefaults() {
        FileUtils.loadDefaults(file, fileName);
    }

    public void replaceDefaults() {
        FileUtils.replaceDefaults(file, fileName);
    }

    public FileConfiguration getConfig() {
        if (fileName.endsWith(".yml")){
            if (config == null){
                config = YamlConfiguration.loadConfiguration(file);
            }
            return config;
        }else{
            Bukkit.getLogger().severe("Config - Could not getConfig from file " + fileName + " file. File must end with .yml");
            return null;
        }
    }

    public void saveConfig() {
        if (fileName.endsWith(".yml")){
            try {
                getConfig().save(file);
            } catch (IOException e) {
                Bukkit.getLogger().severe("Config - Could not save " + fileName + " file. e=" + e.getMessage());
                e.fillInStackTrace();
            }
        }else{
            Bukkit.getLogger().severe("Config - Could not save " + fileName + " file. File must end with .yml");
        }
    }

    public void reload(){
        saveConfig();
        config = YamlConfiguration.loadConfiguration(file);
    }

}
