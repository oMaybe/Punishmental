package net.ruxor.punishments.commands.util;

import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.commands.ConsoleCommand;
import net.ruxor.punishments.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteChatCommand extends ConsoleCommand {
    public MuteChatCommand() {
        super("mutechat", "punishmental.mutechat");
        setUsage(CC.Red + "Usage: /mutechat");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (Punishmental.getInstance().getServerManager().isChatMuted()) {
            Punishmental.getInstance().getServerManager().setChatMuted(false);

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("");
                player.sendMessage(CC.Green + "Chat has been unmuted.");
                player.sendMessage("");
            }

            sender.sendMessage(CC.Green + "Chat has been unmuted.");
        } else {
            Punishmental.getInstance().getServerManager().setChatMuted(true);

            for (Player player : Bukkit.getOnlinePlayers()){
                player.sendMessage("");
                player.sendMessage(CC.Red + "Chat has been muted.");
                player.sendMessage("");
            }

            sender.sendMessage(CC.Red + "Chat has been muted.");
        }
    }
}
