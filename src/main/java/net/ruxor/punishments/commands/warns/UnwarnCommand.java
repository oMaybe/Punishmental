package net.ruxor.punishments.commands.warns;

import net.ruxor.punishments.commands.ConsoleCommand;
import net.ruxor.punishments.util.CC;
import org.bukkit.command.CommandSender;

public class UnwarnCommand extends ConsoleCommand {
    public UnwarnCommand() {
        super("unwarn", "punish.unwarn");
        setUsage(CC.Red + "Usage: /unwarn <player> [reason]");
        // rework, maybe use /unwarn <player> <warnID> [reason] cuz currently a  player has abillion warns and you can't unwarn a specific one
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(getUsage());
            return;
        }

        sender.sendMessage(CC.Red + "This command is not implemented yet!");
    }
}
