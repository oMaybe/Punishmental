package net.ruxor.punishments.commands.misc;

import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.commands.ConsoleCommand;
import net.ruxor.punishments.database.FileConfig;
import net.ruxor.punishments.util.CC;
import net.ruxor.punishments.util.FileUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PunishmentalCommand extends ConsoleCommand {

    public PunishmentalCommand() {
        super("punishmental", (String) FileUtils.getOrDefaultConfig("permission.bypass"));
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /punishmental <fileName> <reload|defaults|replace>");
            return;
        }

        String fileName = args[0].toLowerCase();
        FileUtils.FileAction action = FileUtils.FileAction.valueOf(args[1].toUpperCase());
        switch (fileName){
            case "messages":
                action(action, Punishmental.getInstance().getMessages());
                break;
            case "config":
                action(action, Punishmental.getInstance().getMainConfig());
                break;
            default:
                sender.sendMessage(CC.Red + "Invalid file name.");
                break;
        }
    }

    private void action(FileUtils.FileAction action, FileConfig cfg) {
        switch (action) {
            case RELOAD:
                cfg.reload();
                break;
            case DEFAULTS:
                cfg.loadDefaults();
                break;
            case REPLACE:
                cfg.replaceDefaults();
                break;
        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Arrays.asList("messages", "config");
        }

        if (args.length == 2) {
            return Arrays.asList("reload", "defaults", "replace");
        }

        return new ArrayList<>();
    }
}
