package net.ruxor.punishments.database;

import lombok.Getter;
import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.database.mongo.MongoBackend;
import net.ruxor.punishments.database.mysql.MySQLBackend;
import net.ruxor.punishments.punishment.Punishment;
import net.ruxor.punishments.util.FileUtils;
import org.bukkit.Bukkit;

import java.sql.*;

@Getter
public class DatabaseManager {

    private StorageType database;

    // add more options in future
    public DatabaseManager(){
        if ((boolean) FileUtils.getOrDefaultConfig("mysql.enabled")) {
            database = new MySQLBackend();
        }else if ((boolean) FileUtils.getOrDefaultConfig("mongo.enabled")) {
            database = new MongoBackend();
        }else{
            Bukkit.getLogger().severe("No database type was enabled in the config.");
            Bukkit.getLogger().severe("Please enable either MySQL or MongoDB.");
            Bukkit.getPluginManager().disablePlugin(Punishmental.getInstance());
        }
    }

    public void shutdown() {
        database.shutdown();
    }

    public boolean isConnected() {
        return database != null && database.connected();
    }

    public String getActiveDatabase() {
        if (database instanceof MySQLBackend) {
            return "MySQL";
        }else if (database instanceof MongoBackend) {
            return "MongoDB";
        }else{
            return "Unknown";
        }
    }
}
