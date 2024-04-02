package net.ruxor.punishments.commands.util;

import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.commands.ConsoleCommand;
import net.ruxor.punishments.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LockdownCommand extends ConsoleCommand {
    public LockdownCommand() {
        super("lockdown", "punishmental.lockdown");
        setUsage(CC.Red + "Usage: /lockdown");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (Punishmental.getInstance().getServerManager().isLocked()){
            Punishmental.getInstance().getServerManager().setLocked(false);
            sender.sendMessage(CC.Green + "The server is no longer in lockdown.");
        } else {
            Punishmental.getInstance().getServerManager().setLocked(true);

            for (Player target : Punishmental.getInstance().getServer().getOnlinePlayers()){
                if (!target.hasPermission("punishmental.lockdown.bypass")){
                    target.kickPlayer(CC.Red + "The server is now in lockdown.");
                }
            }

            sender.sendMessage(CC.Red + "The server is now in lockdown.");
        }
    }
}
