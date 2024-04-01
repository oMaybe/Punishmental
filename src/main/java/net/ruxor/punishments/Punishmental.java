package net.ruxor.punishments;

import lombok.Getter;
import net.ruxor.punishments.commands.ban.BanCommand;
import net.ruxor.punishments.commands.ban.BlacklistCommand;
import net.ruxor.punishments.commands.ban.UnbanCommand;
import net.ruxor.punishments.commands.ban.UnblacklistCommand;
import net.ruxor.punishments.commands.misc.PunishmentalCommand;
import net.ruxor.punishments.commands.mutes.MuteCommand;
import net.ruxor.punishments.commands.mutes.UnmuteCommand;
import net.ruxor.punishments.commands.util.AltsCommand;
import net.ruxor.punishments.commands.util.LogsCommand;
import net.ruxor.punishments.commands.warns.UnwarnCommand;
import net.ruxor.punishments.commands.warns.WarnCommand;
import net.ruxor.punishments.data.ProfileHandler;
import net.ruxor.punishments.database.DatabaseManager;
import net.ruxor.punishments.database.FileConfig;
import net.ruxor.punishments.listeners.ChatListener;
import net.ruxor.punishments.listeners.GenericListener;
import net.ruxor.punishments.profiler.NewProfiler;
import net.ruxor.punishments.util.CustomLogger;
import net.ruxor.punishments.util.Tasks;
import net.ruxor.punishments.util.spigui.SpiGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Punishmental extends JavaPlugin {

    @Getter
    private static Punishmental instance = null;

    // runs factory for things like mysql, mongo, redis
    @Getter private ExecutorService service;

    @Getter private NewProfiler profiler;

    @Getter private ProfileHandler profileHandler;
    @Getter private DatabaseManager databaseManager;

    @Getter private FileConfig mainConfig;
    @Getter private FileConfig messages;

    @Getter private long loadTime = 0;
    @Getter private SpiGUI spiGUI;

    @Override
    public void onEnable() {
        profiler = new NewProfiler();
        profiler.start("Punishmental - Load Time");

        instance = this;

        service =  Executors.newFixedThreadPool(2);
        spiGUI = new SpiGUI(this);

        loadConfig();
        registerCommands();
        registerEvents();

        databaseManager = new DatabaseManager();
        profileHandler = new ProfileHandler();

        Tasks.runLater(this, () -> {
            loadTime = System.currentTimeMillis();
            profiler.stop("Punishmental - Load Time");

            this.log("&e==========&6=====================&e==========");
            this.log("&aCongratulations! The plugin is now fully loaded!");
            this.log("");
            this.log("&aVersion&7: &3v" + this.getDescription().getVersion());
            this.log("&aName&7: &3" + this.getDescription().getName());
            this.log(" ");
            this.log("&fDatabase&7: " + (this.databaseManager.isConnected() ? "&aConnected" : "&cDisconnected"));
            this.log("&fDatabase Type: &a" + (this.databaseManager.getActiveDatabase()));
            this.log("&fServer Status: " + "&aOnline");
            this.log("");
            this.log("&aLoad Time: &3" + profiler.differenceTime("Punishmental - Load Time"));
            this.log("&e==========&6=====================&e==========");
        }, 20L * 7);
    }

    @Override
    public void onDisable() {
        profiler.start("Punishmental - Disable Time");

        databaseManager.shutdown();

        mainConfig.saveConfig();
        messages.saveConfig();

        service.shutdown();
        service = null;

        Tasks.runLater(this, () -> {
            profiler.stop("Punishmental - Disable Time");
            this.log("&e==========&6=====================&e==========");
            this.log("&aCongratulations! The plugin is now fully loaded!");
            this.log("");
            this.log("&fVersion&7: &3v" + this.getDescription().getVersion());
            this.log("&fName&7: &3" + this.getDescription().getName());
            this.log(" ");
            this.log("&fDatabase&7: " + (this.databaseManager.isConnected() ? "&aConnected" : "&cDisconnected"));
            this.log("&fDatabase in use&7: &3" + this.databaseManager.getActiveDatabase());
            this.log("&fServer Status&7: " + "&cOffline");
            this.log("");
            this.log("&aLoad Time: &3" + profiler.differenceTime("Punishmental - Disable Time"));
            this.log("&e==========&6=====================&e==========");
        }, 20L * 3);
    }

    public void log(String message){
        CustomLogger.log(message);
    }

    private void registerCommands(){
        List<? extends Command> commands = Arrays.asList(
                new BanCommand(),
                new UnbanCommand(),

                new BlacklistCommand(),
                new UnblacklistCommand(),

                new MuteCommand(),
                new UnmuteCommand(),

                new WarnCommand(),
                new UnwarnCommand(),

                new PunishmentalCommand(),
                new LogsCommand(),
                new AltsCommand()
        );

        commands.forEach(cmd -> {
            ((CraftServer) Bukkit.getServer()).getCommandMap().register(cmd.getName(), cmd);
        });
    }

    private void registerEvents() {
        List<? extends Listener> listeners = Arrays.asList(
                new GenericListener(),
                new ChatListener()
        );

        listeners.forEach(listener -> {
            Bukkit.getPluginManager().registerEvents(listener, this);
        });
    }

    private void loadConfig(){
        if (!getDataFolder().exists()){
            boolean parentFolder = getDataFolder().mkdirs();
            if (parentFolder){
                Bukkit.getLogger().info("Config - Created a parent folder to hold all config files!");
            }else{
                Bukkit.getLogger().info("Config - Config parent folder appears to exist.");
            }
        }

        this.mainConfig = new FileConfig("config.yml");
        this.messages = new FileConfig("messages.yml");
    }

    public Reader getStringResource(String file){
        return super.getTextResource(file);
    }
}
